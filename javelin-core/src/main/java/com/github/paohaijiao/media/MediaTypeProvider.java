package com.github.paohaijiao.media;

import com.github.paohaijiao.spi.anno.Priority;
import com.github.paohaijiao.spi.constants.PriorityConstants;

import java.util.List;

@Priority(PriorityConstants.BUSINESS_MEDIUM)
public interface MediaTypeProvider {

    List<MediaTypeDefinition> provideMediaTypes();

}
