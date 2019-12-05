# readme

## sms数据结构

```
_id: 序号，不能作为唯一值，删除后可能再次生成
thread_id: 对话
address: 对方号码
person: 对方名称，如果不存在通讯录为null
date: 时间戳(13位)，可以将此作为阈值
date_sent: 
protocol: 协议 0、SMS_PROTO, 1、MMS_PROTO
read: 读取状态 0 表示未读 1表示已读
status: 状态 -1接收，0完成，64等待，128失败
type: 1表示接收 2表示发出
reply_path_present: 
subject: 
body: 内容
service_center: 短信服务中心号码编号
locked: 
sub_id: 
error_code: 
creator: 
seen: 
```

## todo

* 定时清理短信
* 定时ping
* 写入最后一条短信时间

17205088874