package facade.dto;


import org.springframework.hateoas.ResourceSupport;

import java.math.BigDecimal;

public class Simple extends ResourceSupport {

    private String content;
    private BigDecimal result;

    public Simple() {
    }

    public Simple(String content) {
        this.content = content;
    }

    public Simple(BigDecimal result) {
        this.result = result;
    }

    public BigDecimal getResult() {
        return result;
    }

    public void setResult(BigDecimal result) {
        this.result = result;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
