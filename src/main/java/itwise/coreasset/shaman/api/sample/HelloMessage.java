package itwise.coreasset.shaman.api.sample;

import java.io.Serializable;

/**
 * Created by chanwook on 2014. 7. 29..
 */
public class HelloMessage implements Serializable {
    private String name;

    public HelloMessage() {
    }

    public HelloMessage(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
