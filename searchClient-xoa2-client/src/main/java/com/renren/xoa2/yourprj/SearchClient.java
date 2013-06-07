package com.renren.xoa2.yourprj;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.renren.xoa2.client.ServiceFactory;
import com.renren.xoa2.ntc.search.ISearchClient;

/**
 * 一个简单的服务使用方式，XOA2.0 的服务应该采用类似的方式使用
 * 
 * @author YOU
 */
public class SearchClient {
    protected static final Log log = LogFactory.getLog(SearchClient.class);
    
    public static void main(String[] args) {
        ISearchClient client = ServiceFactory.getService(ISearchClient.class);
        
        // aTest Add
        
        System.exit(0);
    }
}
