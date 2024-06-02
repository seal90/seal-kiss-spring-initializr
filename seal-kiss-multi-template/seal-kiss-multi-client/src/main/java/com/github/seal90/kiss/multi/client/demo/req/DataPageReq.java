package com.github.seal90.kiss.multi.client.demo.req;

import io.github.seal90.kiss.base.request.PageRequest;
import lombok.Data;

import java.util.List;

@Data
public class DataPageReq extends PageRequest {

    private List<String> stringAttrs;
}
