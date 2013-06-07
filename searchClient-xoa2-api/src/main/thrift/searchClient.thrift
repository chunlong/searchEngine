# helloWorld service IDL
# YOU@renren-inc.com

# !!! CHANGE THE NAMESPACE BELOW !!!
namespace java com.renren.xoa2.ntc.search
namespace cpp xoa2.ntc.search
include "base/xoabase.thrift"
struct UpdateFieldsRequest {
  1: required map<string,string> field_value;
  2: required i32 id;
}

struct UpdateFieldsResponse {
  1: optional bool result;
  2: optional xoabase.BaseResponse baseRep;
}

struct CreateRequest {
  1: required map<string,string> index;
  2: required i32 id;
}

struct CreateResponse {
  1: optional bool result;
  2: optional xoabase.BaseResponse baseRep;
}

struct SearchRequest {
  1: required string queryString;
  2: required i32 offset;
  3: required i32 count
}

struct SearchResponse {
  1: optional list<i32> result;
  2: optional xoabase.BaseResponse baseRep;
}

service SearchClient {
  /**
   *创建索引
   */
  CreateResponse create(1: CreateRequest req);

  /**
   * 更新索引
   */
  UpdateFieldsResponse updateFields(1: UpdateFieldsRequest req);

 /**
   *搜索
   */
  SearchResponse search(1: SearchRequest req);
}