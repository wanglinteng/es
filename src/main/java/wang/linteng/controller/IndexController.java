package wang.linteng.controller;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import wang.linteng.util.EsTool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.elasticsearch.index.query.QueryBuilders.matchQuery;

@Controller
@RequestMapping(value = "/news/*")
public class IndexController {

    private TransportClient client;

    @RequestMapping(value = "index")
    public String index() {
        return "views/index";
    }

    @RequestMapping(value = "search")
    public ModelAndView search(String key,Integer page) {
        ModelAndView mv = new ModelAndView();
        Map<String,Object> map = match(key,page);
        mv.addObject("list",map.get("list"));
        mv.addObject("total",map.get("total"));
        mv.addObject("key",key);
        mv.addObject("page",page);
        mv.addObject("all",Math.ceil(Integer.valueOf(map.get("total").toString())/10));
        mv.setViewName("views/search");
        return mv;
    }

    @RequestMapping(value = "test")
    public String test(){
        QueryBuilder qb = matchQuery ("content","刘德华");
        return "views/index";
    }

    protected  Map<String,Object> match(String key,Integer page){
        TransportClient client = EsTool.getClient();
        QueryBuilder query = matchQuery("content", key);

        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.preTags("<span class=\"text-danger\">");
        highlightBuilder.postTags("</span>");
        highlightBuilder.field("content");

        SearchResponse response = client.prepareSearch(EsTool.getIndexName())
                .setQuery(query)
                .highlighter(highlightBuilder)
                .setFrom(page*10)
                .setSize(10)
                .setExplain(true)
                .execute()
                .actionGet();
        SearchHits searchHits = response.getHits();
        long total = searchHits.getTotalHits();// 符合要求记录数
        List<Map> list = new ArrayList();// 符合要求数据集
        for(SearchHit hit : searchHits){
            Map<String,Object> item = hit.getSource();
            Text[] fragments = hit.getHighlightFields().get("content").fragments();
            String highlightContent = "";
            for(Text text : fragments){
                highlightContent+=text;
            }
            item.put("content",highlightContent);
           list.add(item);
        }
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("total",total);
        map.put("list",list);
        EsTool.closeClient();
        return map;
    }
}
