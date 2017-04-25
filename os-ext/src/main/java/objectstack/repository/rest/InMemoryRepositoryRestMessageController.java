package objectstack.repository.rest;

import objectstack.api.Message;
import objectstack.api.MessageRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by tangfeixiong on 3/12/17.
 */
@RestController
@RequestMapping("/apis/v1")
/*
 * POST: curl  -H "Content-type: application/json" -X POST -d '{"id":10,"text":"AAAA","summary":"BBBB"}'  http://localhost:9090/apis/v1
 * PUT: curl  -H "Content-type: application/json" -X PUT -d '{"id":10,"text":"aaaaa","summary":"bbbbb"}'  http://localhost:9090/apis/v1/10
 * GET: curl http://localhost:9090/apis/v1/accounts
 * GET: curl http://localhost:9090/v1/accounts/<account>
 * DELETE: curl -X DELETE http://localhost:9090/10
 *
 * GET: curl http://localhost:9090/v1/accounts/<account>/containers
 * GET: curl http://localhost:9090/v1/accounts/<account>/containers/<container>
 * GET: curl http://localhost:9090/v1/accounts/<account>/containers/<container>/objects
 * GET: curl http://localhost:9090/v1/accounts/<account>/containers/<container>/objects/<object>
 * OpenStack Swift
 * GET /v1/{account}
 *     Show account details and list containers
curl -i $publicURL?format=json -X GET -H "X-Auth-Token: $token"
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

 */
public class InMemoryRepositoryRestMessageController {
        private final MessageRepository messageRepository;

        @Autowired
        public InMemoryRepositoryRestMessageController(MessageRepository messageRepository) {
                this.messageRepository = messageRepository;
                messageRepository.save(new Message(null,"Hello","World"));
                messageRepository.save(new Message(null,"Hi","Universe"));
                messageRepository.save(new Message(null,"Hola","OpenShift"));
        }

        private final AtomicLong counter = new AtomicLong();

        @RequestMapping(method= RequestMethod.POST)
        public @ResponseBody
        Message create(@RequestBody Message msg) {
                System.out.println("InMemoryRepositoryRestMessageController.create: " + msg);
                messageRepository.save(msg);
                return msg;
        }

        @RequestMapping(method=RequestMethod.GET)
        public @ResponseBody Iterable<Message> getAll() {
                System.out.println("InMemoryRepositoryRestMessageController.getAll" );
                return messageRepository.findAllIt();
        }

        @RequestMapping(method=RequestMethod.DELETE, value="{id}")
        public void delete(@PathVariable Long id) {
                System.out.println("InMemoryRepositoryRestMessageController.delete: " + id);
                messageRepository.delete(id);
        }

        @RequestMapping(method=RequestMethod.PUT, value="{id}")
        public @ResponseBody Message update(@PathVariable String id, @RequestBody Message contact) {
                System.out.println("InMemoryRepositoryRestMessageController.update: " + contact);
                if (contact.getId() != null) {
                        Message msg = messageRepository.findMessage(contact.getId());
                        msg.setSummary(contact.getSummary());
                        msg.setText(contact.getText());
                        messageRepository.save(msg);
                        return msg;
                }else
                {
                        Message msg = messageRepository.save(contact);
                        return msg;
                }
        }


        @RequestMapping(method=RequestMethod.GET, value="{id}")
        public @ResponseBody Message getById(@PathVariable("id") Long id) {
                System.out.println("InMemoryRepositoryRestMessageController.getById: " + id);
                return messageRepository.findMessage(id);
        }

}
