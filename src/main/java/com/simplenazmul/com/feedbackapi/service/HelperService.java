package com.simplenazmul.com.feedbackapi.service;

import org.springframework.data.domain.Page;
import org.springframework.lang.Nullable;

import com.simplenazmul.com.feedbackapi.helper.Response;

import java.util.List;

public interface HelperService {

    Response getResponse(Page<?> contents, @Nullable List<?> resources, @Nullable Long totalElements);

}