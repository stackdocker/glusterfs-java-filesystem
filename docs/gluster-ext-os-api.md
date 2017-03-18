Gluster Extension Object Storage API
======================================

API

* HTTP JSON RPC

Upload

* MIME: multipart/form-data

Download

* File server

API Details
----------------
    
### _Tenant_ Entry

__READ__ 

Note: tenant only get entry info himself

    GET  /v1/{account}

Example: `curl -i $publicURL?format=json -X GET -H "X-Auth-Token: $token"`

HTTP Response Status:

* OK (200), success
* No Content (204), success

HTTP Response Header:

    HTTP/1.1 200 OK
    Content-Length: 96
    X-Account-Object-Count: 1
    X-Timestamp: 1389453423.35964
    X-Account-Meta-Subject: Literature
    X-Account-Bytes-Used: 14
    X-Account-Container-Count: 2
    Content-Type: application/json; charset=utf-8
    Accept-Ranges: bytes
    X-Trans-Id: tx274a77a8975c4a66aeb24-0052d95365
    X-Openstack-Request-Id: tx274a77a8975c4a66aeb24-0052d95365
    Date: Fri, 17 Jan 2014 15:59:33 GMT
    
HTTP Response Body:

    [
        {
            "count": 0,
            "bytes": 0,
            "name": "janeausten",
            "last_modified": "2013-11-19T20:08:13.283452"
        },
        {
            "count": 1,
            "bytes": 14,
            "name": "marktwain",
            "last_modified": "2016-04-29T16:23:50.460230"
        }
    ]
    

__Create__

Note: admin role required

    PUT  /v1/{account}

__Update metadata__

Note: admin role required

    PUT  /v1/{account}

__Delete__

Note: admin role required

    DELETE  /v1/{account}


### _Catalogue_ Entry

__Read__

Note: tenant authorization only

    GET  /v1/{account}/{container}
    
HTTP Response

Header:

    HTTP/1.1 200 OK
    Content-Length: 341
    X-Container-Object-Count: 2
    Accept-Ranges: bytes
    X-Container-Meta-Book: TomSawyer
    X-Timestamp: 1389727543.65372
    X-Container-Bytes-Used: 26
    Content-Type: application/json; charset=utf-8
    X-Trans-Id: tx26377fe5fab74869825d1-0052d6bdff
    X-Openstack-Request-Id: tx26377fe5fab74869825d1-0052d6bdff
    Date: Wed, 15 Jan 2014 16:57:35 GMT

Body:

    [
        {
            "hash": "451e372e48e0f6b1114fa0724aa79fa1",
            "last_modified": "2014-01-15T16:41:49.390270",
            "bytes": 14,
            "name": "goodbye",
            "content_type": "application/octet-stream"
        },
        {
            "hash": "ed076287532e86365e841e92bfc50d8c",
            "last_modified": "2014-01-15T16:37:43.427570",
            "bytes": 12,
            "name": "helloworld",
            "content_type": "application/octet-stream"
        }
    ]
    

__Create__

Note: tenant authorization only

    PUT  /v1/{account}/{container}
    
Example: `curl -i $publicURL/steven -X PUT -H "Content-Length: 0" -H "X-Auth-Token: $token"`
    
HTTP Response

Header

    HTTP/1.1 201 Created
    Content-Length: 0
    Content-Type: text/html; charset=UTF-8
    X-Trans-Id: tx7f6b7fa09bc2443a94df0-0052d58b56
    X-Openstack-Request-Id: tx7f6b7fa09bc2443a94df0-0052d58b56
    Date: Tue, 14 Jan 2014 19:09:10 GMT
    
__Update metadata__

Note: tenant authorization only





