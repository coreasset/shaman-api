package itwise.coreasset.shaman.api.sample;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by chanwook on 2014. 7. 28..
 */
@RestController
public class HelloWorldApi {

    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    public HelloMessage hello() {
        return new HelloMessage("chanwook");
    }
}
