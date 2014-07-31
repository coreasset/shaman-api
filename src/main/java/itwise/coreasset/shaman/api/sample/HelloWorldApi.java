package itwise.coreasset.shaman.api.sample;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by chanwook on 2014. 7. 28..
 */
@RestController
public class HelloWorldApi {

    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    public HttpEntity<HelloMessage> hello() {
        HelloMessage msg = new HelloMessage("chanwook");
        return new ResponseEntity<HelloMessage>(msg, HttpStatus.OK);
    }

    @RequestMapping(value = "/hello", method = RequestMethod.POST)
    public HttpEntity<HelloMessage> create(@RequestBody HelloMessage msg) {

        return new ResponseEntity<HelloMessage>(msg, HttpStatus.CREATED);
    }
}
