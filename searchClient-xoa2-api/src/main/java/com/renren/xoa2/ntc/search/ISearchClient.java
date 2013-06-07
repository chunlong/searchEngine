package com.renren.xoa2.ntc.search;

import com.renren.xoa2.annotation.XoaService;

/**
 * 请注意：<br><br>
 * 1. @XoaService 里面的字符串即为服务的 ServiceId，规范见：
 *      <pre><b>http://wiki.d.xiaonei.com/pages/viewpage.action?pageId=16089190</b></pre>
 * <br>
 * 2. 接口继承 thrift 生成文件的全部函数，去除异常
 * 
 * @author YOU
 */
@XoaService("search.ntc.tech.xoa2")
public interface ISearchClient extends SearchClient.Iface {

    @Override
    public UpdateFieldsResponse updateFields(UpdateFieldsRequest req);

    @Override
    public CreateResponse create(CreateRequest req);

    @Override
    public SearchResponse search(SearchRequest req);

}
