package netty.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import net.sf.json.JSONObject;

import netty.http.annotion.ResponseBody;
import netty.http.route.RouteMethod;
import netty.http.utils.BasicTypeChecker;
import netty.http.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author RAY
 * @descriptions
 * @since 2020/3/29
 */
@ChannelHandler.Sharable
public class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private static final String APPLICATION_JSON = "application/json";
    private static final String TEXT_PLAIN = "text/plain";
    private static final String nullStr = "";
    private static Logger logger = LoggerFactory.getLogger(HttpServerHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest defaultHttpRequest) throws Exception {
        String path = path(defaultHttpRequest);
        Map<String, List<String>> parameters = parameters(defaultHttpRequest);
        logger.info("path-> {}", path);
        logger.info("parameters-> {}", parameters);
        Method method = RouteMethod.route(path);
        Object code = nullStr;
        String content_type = TEXT_PLAIN;
        if (method != null) {
            ResponseBody annotation = method.getAnnotation(ResponseBody.class);
            Class<?> type = method.getReturnType();
            if (annotation != null && !BasicTypeChecker.isPrimitive(type)) {
                content_type = APPLICATION_JSON;
            }
            String name = method.getDeclaringClass().getName();
            try {
                code = method.invoke(RouteMethod.bean(name),
                        RouteMethod.parseRouteParameter(method, parameters));
                if (code != null && annotation != null) {
                    if (type != String.class) {
                        code = JsonUtils.objectToJson(code);
                    }
                }
                if (code == null) {
                    code = nullStr;
                }
            } catch (Exception e) {
                e.printStackTrace();
                code = e;
                content_type = TEXT_PLAIN;
            }
        } else {
            code = "request url not exist";
        }
        ByteBuf buf = Unpooled.wrappedBuffer(code.toString().getBytes(StandardCharsets.UTF_8));
        DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buf);
        HttpHeaders headers = response.headers();
        headers.setInt(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
        headers.set(HttpHeaderNames.CONTENT_TYPE, content_type);
        ctx.channel().writeAndFlush(response);
    }

    /**
     * 拿到请求参数
     *
     * @param defaultHttpRequest
     * @return
     * @throws Exception
     */
    public Map<String, List<String>> parameters(FullHttpRequest defaultHttpRequest) throws Exception {
        if (defaultHttpRequest.method() == HttpMethod.GET) {
            QueryStringDecoder queryStringDecoder = new QueryStringDecoder(URLDecoder.decode(defaultHttpRequest.uri(), "utf-8"));
            return queryStringDecoder.parameters();
        }
        if (defaultHttpRequest.method() == HttpMethod.POST) {
            HttpHeaders headers = defaultHttpRequest.headers();
            Map<String, List<String>> map = new HashMap<>();
            if (headers.get(HttpHeaderNames.CONTENT_TYPE).contains(APPLICATION_JSON)) {
                String s = defaultHttpRequest.content().toString(StandardCharsets.UTF_8);
                JSONObject object = JSONObject.fromObject(s);
                Set keySet = object.keySet();
                for (Object o : keySet) {
                    List<String> list = new ArrayList<>();
                    list.add(String.valueOf(object.get(o)));
                    map.put(String.valueOf(o), list);
                }
                return map;
            }
            HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(defaultHttpRequest);
            decoder.offer(defaultHttpRequest);
            List<InterfaceHttpData> datas = decoder.getBodyHttpDatas();
            for (InterfaceHttpData data : datas) {
                List<String> list = new ArrayList<>();
                Attribute attribute = (Attribute) data;
                list.add(attribute.getValue());
                map.put(attribute.getName(), list);
            }
            return map;
        }
        return null;
    }

    /**
     * 找到访问路径
     *
     * @param defaultHttpRequest
     * @return
     * @throws UnsupportedEncodingException
     */
    public String path(FullHttpRequest defaultHttpRequest) throws UnsupportedEncodingException {
        if (defaultHttpRequest.method() == HttpMethod.GET) {
            QueryStringDecoder queryStringDecoder = new QueryStringDecoder(URLDecoder.decode(defaultHttpRequest.uri(), "utf-8"));
            return queryStringDecoder.path();
        }
        return defaultHttpRequest.uri();
    }

}
