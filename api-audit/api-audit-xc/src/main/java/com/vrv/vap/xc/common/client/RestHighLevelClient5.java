//package com.vrv.vap.xc.common.client;
//
//import org.apache.http.Header;
//import org.elasticsearch.action.ActionRequest;
//import org.elasticsearch.action.ActionRequestValidationException;
//import org.elasticsearch.action.search.SearchRequest;
//import org.elasticsearch.action.search.SearchResponse;
//import org.elasticsearch.client.*;
//import org.elasticsearch.common.CheckedConsumer;
//import org.elasticsearch.common.CheckedFunction;
//import org.elasticsearch.common.xcontent.NamedXContentRegistry;
//import org.elasticsearch.common.xcontent.XContentParser;
//
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Set;
//
//import static java.util.Collections.emptySet;
//
//public class RestHighLevelClient5 extends RestHighLevelClient {
//    public RestHighLevelClient5(RestClientBuilder restClientBuilder) {
//        super(restClientBuilder);
//    }
//
//    protected RestHighLevelClient5(RestClientBuilder restClientBuilder, List<NamedXContentRegistry.Entry> namedXContentEntries) {
//        super(restClientBuilder, namedXContentEntries);
//    }
//
//    protected RestHighLevelClient5(RestClient restClient, CheckedConsumer<RestClient, IOException> doClose, List<NamedXContentRegistry.Entry> namedXContentEntries) {
//        super(restClient, doClose, namedXContentEntries);
//    }
//
//    /**
//     * Executes a search using the Search API
//     * <p>
//     * See <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/search-search.html">Search API on elastic.co</a>
//     */
//    public final SearchResponse search5(SearchRequest searchRequest, Header... headers) throws IOException {
//        return performRequestAndParseEntity5(searchRequest, Request5::search, SearchResponse::fromXContent, emptySet(), headers);
//    }
//
//
//    protected final <Req extends ActionRequest, Resp> Resp performRequestAndParseEntity5(Req request,
//                                                                                         CheckedFunction<Req, Request5, IOException> requestConverter,
//                                                                                         CheckedFunction<XContentParser, Resp, IOException> entityParser,
//                                                                                         Set<Integer> ignores, Header... headers) throws IOException {
//        return performRequest5(request, requestConverter, (response) -> parseEntity(response.getEntity(), entityParser), ignores, headers);
//    }
//
//    protected final <Req extends ActionRequest, Resp> Resp performRequest5(Req request,
//                                                                           CheckedFunction<Req, Request5, IOException> requestConverter,
//                                                                           CheckedFunction<Response, Resp, IOException> responseConverter,
//                                                                           Set<Integer> ignores, Header... headers) throws IOException {
//        ActionRequestValidationException validationException = request.validate();
//        if (validationException != null) {
//            throw validationException;
//        }
//        Request5 req = requestConverter.apply(request);
//        Response response;
//        try {
//            response = getLowLevelClient().performRequest(req.getMethod(), req.getEndpoint(), new HashMap<>(), req.getEntity(), headers);
//        } catch (ResponseException e) {
//            if (ignores.contains(e.getResponse().getStatusLine().getStatusCode())) {
//                try {
//                    return responseConverter.apply(e.getResponse());
//                } catch (Exception innerException) {
//                    //the exception is ignored as we now try to parse the response as an error.
//                    //this covers cases like get where 404 can either be a valid document not found response,
//                    //or an error for which parsing is completely different. We try to consider the 404 response as a valid one
//                    //first. If parsing of the response breaks, we fall back to parsing it as an error.
//                    throw parseResponseException(e);
//                }
//            }
//            throw parseResponseException(e);
//        }
//
//        try {
//            return responseConverter.apply(response);
//        } catch (Exception e) {
//            throw new IOException("Unable to parse response body for " + response, e);
//        }
//    }
//
//
//}
