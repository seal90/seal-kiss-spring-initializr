package com.github.seal90.kiss.multi.client.demo.resp;

import com.github.seal90.kiss.multi.client.demo.vo.DemoVO;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class DataReturnMultiResp {

    private List<DemoVO> demoDTOs;

}
