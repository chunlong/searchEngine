# basic defination:
namespace java com.renren.xoa2
namespace cpp xoa2

struct ErrorInfo {
  1: required i32 code;
  2: required string msg;
}

struct BaseResponse {
  1: optional ErrorInfo errorInfo;
}