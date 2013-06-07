package com.renren.xoa2.ntc.search;


import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.renren.xoa2.ntc.search.CreateRequest;
import com.renren.xoa2.ntc.search.CreateResponse;
import com.renren.xoa2.ntc.search.ISearchClient;
import com.renren.xoa2.ntc.search.SearchRequest;
import com.renren.xoa2.ntc.search.SearchResponse;
import com.renren.xoa2.ntc.search.UpdateFieldsRequest;
import com.renren.xoa2.ntc.search.UpdateFieldsResponse;

public class SearchClientImpl implements ISearchClient{
    private static final Logger logger = LoggerFactory.getLogger(SearchClientImpl.class);
	private static Directory indexPath = null;
	private static IndexWriter indexWrite = null;
	private static Analyzer analyzer = null;
	private static IndexReader reader = null;
	private static QueryParser queryParserName = null;
	private static Searcher searcher = null;
	private static final String FIELD_ID = "id";
	private static final String FIELD_NAME = "name";
	private static int count = 0;
	private static TimerTask task= null;
	private static Lock w;
	private static Lock r;

	static {
		try{
			logger.info("start init!!");
			final ReadWriteLock lock=new ReentrantReadWriteLock();
			r = lock.readLock();
			w = lock.writeLock();
			Set<String> f =new HashSet<String>();
			indexPath = new SimpleFSDirectory(new File("/data/web/search/index"));
			analyzer = new StandardAnalyzer(Version.LUCENE_CURRENT,f);
			indexWrite = new IndexWriter(indexPath, analyzer, false,new IndexWriter.MaxFieldLength(20)); 
			indexWrite.setMaxBufferedDocs(1000); 
			reader = IndexReader.open(indexPath);
			searcher = new IndexSearcher(reader);  
			operateOnTime();
		}catch(Exception e){
			logger.info("init search failed!");
			e.printStackTrace();
		}
	 }
	
	@Override
	public UpdateFieldsResponse updateFields(UpdateFieldsRequest upReq){
		UpdateFieldsResponse upRes = new UpdateFieldsResponse();
		int id = upReq.getId();
		Map<String,String> field_value =upReq.getField_value();
		Set<String> s = new HashSet<String>();
		s = field_value.keySet();
		Document doc = new Document();
		if(s.size() == 0){
			upRes.setResult(false);
			return upRes;
		}
		if(id == 0 ||String.valueOf(id)==null){
			upRes.setResult(false);
			return upRes;
		}
		for(String field :s){
			Field fld;
			String value = field_value.get(field);
			logger.info("update index :field:"+ field+"  value:"+value);
	         fld = new Field(field, value, Field.Store.YES,Field.Index.ANALYZED, Field.TermVector.WITH_POSITIONS_OFFSETS);  
	         doc.add(fld);
		}
		try {
			r.lock();
			indexWrite.updateDocument(new Term(FIELD_ID,Integer.toString(id)), doc);
			logger.info("内存文档数"+indexWrite.numRamDocs());
		} catch (CorruptIndexException e) {
			e.printStackTrace();
			upRes.setResult(false);
			return upRes;
		
		} catch (IOException e) {
			e.printStackTrace();
			upRes.setResult(false);
			return upRes;
		} finally{
			r.unlock();
		}
		upRes.setResult(true);
		return upRes;
	}

	@Override
	public CreateResponse create(CreateRequest createReq){
		Set<String> s = new HashSet<String>();
		CreateResponse createRes = new CreateResponse();
		int id = createReq.getId();
		Map<String,String> field_value =createReq.getIndex();
		s = field_value.keySet();
		Document doc = new Document();
		if(s.size() == 0){
			createRes.setResult(false);
			return createRes;
		}
		if(id == 0 ||String.valueOf(id)==null){
			createRes.setResult(false);
			return createRes;
		}

		// 搜索 
		for(String field :s){
			Field fld;
			String value = field_value.get(field);
			logger.info("create index :field:"+ field+"  value:"+value);
	         fld = new Field(field, value, Field.Store.YES,Field.Index.ANALYZED, Field.TermVector.WITH_POSITIONS_OFFSETS);  
	         doc.add(fld);
		}
		try{
			r.lock();
			indexWrite.addDocument(doc);
			logger.info("内存文档数"+indexWrite.numRamDocs());
		} catch (CorruptIndexException e) {
			e.printStackTrace();
			createRes.setResult(false);
			return createRes;
		
		} catch (IOException e) {
			e.printStackTrace();
			createRes.setResult(false);
			return createRes;
		}finally{
			r.unlock();
		}
		createRes.setResult(true);
		return createRes;
	}
	
	@Override
	public SearchResponse search(SearchRequest searchReq){
		Query query = null;
		SearchResponse searchRes = new SearchResponse();
		List<Integer> result = new ArrayList<Integer>();
		String queryString = searchReq.getQueryString();
		int count = searchReq.getCount();
		int offset = searchReq.getOffset();
		logger.info("queryString:"+queryString);
		try{
			queryParserName = new QueryParser(Version.LUCENE_CURRENT,FIELD_NAME , analyzer);
			query = queryParserName.parse(queryString);
			logger.info(query.toString());
			if(query instanceof PhraseQuery ){
				logger.info("is PhraseQusery");
				((PhraseQuery)query).setSlop(5);
			}
			if(query instanceof BooleanQuery){
				processPhraseQuery(query);
			}
			TopScoreDocCollector results = TopScoreDocCollector.create(offset+count,true);
	       	searcher.search(query, results); 
	        TopDocs topDocs = results.topDocs(offset, count);
	        if (topDocs.totalHits == 0) {   
	        	searchRes.setResult(result);
                return searchRes;   
            } 
	        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        	for(int i=0;i<scoreDocs.length;i++){
        		int docId = scoreDocs[i].doc;
        		Document document = searcher.doc(docId);
        		String id = document.get(FIELD_ID);
        		if(id == null){
        			continue;
        		}
        		try{
        			result.add(Integer.parseInt(id));
        		}catch(Exception e){
        			e.printStackTrace();
        		}	            		
        	}
		}catch(Exception e){
			e.printStackTrace();
		}
		searchRes.setResult(result);
        return searchRes;   
	}
	public String praseQueryString(String queryString){
    	String regEx="[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@_#￥%……&*（）——+|{}【】‘；：”“’。，、？  ]";  
    	Pattern p = Pattern.compile(regEx);     
    	Matcher m = p.matcher(queryString);     
    	return m.replaceAll("").trim();
	}
	private static void processPhraseQuery(Query rootQuery) {
		BooleanClause[] clauses = ((BooleanQuery) rootQuery).getClauses();
		for (BooleanClause clause : clauses) {
			Query subQuery = clause.getQuery();
			if (subQuery instanceof PhraseQuery) {
				PhraseQuery pq = (PhraseQuery) subQuery;
				pq.setSlop(Integer.MAX_VALUE);
				logger.info(pq.toString());
			} else if (subQuery instanceof BooleanQuery) {
				processPhraseQuery(subQuery);
			} else {
				continue;
			}
		}
	}
	private static void operateOnTime() {
	       // 定时执行的任务
	       logger.info("start crontab task!!");
	        task = new TimerTask() {
	           @Override
	           public void run() {
	        	   try {
	        		 w.lock();
	       			indexWrite.commit();
	       		} catch (CorruptIndexException e) {
	       			e.printStackTrace();
	       			logger.info("commit changes to index file failed!!");
	       		} catch (IOException e) {
	       			e.printStackTrace();
	       			logger.info("commit changes to index file failed!!");
	       		}finally{
	       			w.unlock();
	       		}
	       		try {
	       			Searcher tmp ;
	       			reader = reader.reopen();
	       			tmp = searcher;
	       			searcher = new IndexSearcher(reader); 
	       			Thread.sleep(1000);
	       			tmp.close();
	       		} catch (CorruptIndexException e) {
	       			e.printStackTrace();
	       		} catch (IOException e) {
	       			e.printStackTrace();
	       		}catch(Throwable e){
	       			e.printStackTrace();
	       		}
	       		logger.info( " 第 " + ++ count + " 次执行 " );
	           }
	       };
	       Timer timer = new Timer();
	       timer.schedule(task, Calendar.getInstance ().getTime(), 10*6* 1000);
	    }
	
}
