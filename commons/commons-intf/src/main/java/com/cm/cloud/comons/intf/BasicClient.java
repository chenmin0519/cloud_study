package com.cm.cloud.comons.intf;

import com.cm.cloud.commons.pojo.dto.PageQuery;
import com.cm.cloud.commons.pojo.dto.QueryExample;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;


public interface BasicClient<T> {

    /**
     * 新增
     *
     * @return
     */
    @RequestMapping(value = "", method = RequestMethod.POST)
    T save(@RequestBody T model);

    /**
     * 全量修改
     *
     * @return
     */
    @RequestMapping(value = "", method = RequestMethod.PUT)
    T update(@RequestBody T model);

    /**
     * 局部修改
     *
     * @return
     */
    @RequestMapping(value = "", method = RequestMethod.PATCH)
    T updateSelective(@RequestBody T model);

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    boolean delete(@PathVariable("id") Long id);

    //查询
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    T loadById(@PathVariable("id") Long id);

    /**
     * 单行查询
     *
     * @return
     */
    @RequestMapping(value = "/_search_one", method = RequestMethod.POST)
    T loadOne(@RequestBody T model);

    @RequestMapping(value = "/_search", method = RequestMethod.POST)
    List<T> loadList(@RequestBody T model);

    @ApiOperation("分页查询")
    @RequestMapping(value = "/_search_page", method = RequestMethod.POST)
    PageInfo<T> loadPage(PageQuery<T> query);

    @ApiOperation("加载行数")
    @RequestMapping(value = "/_load_count", method = RequestMethod.POST)
    int loadCount(T model);

    @RequestMapping(value = "/_search_by_example", method = RequestMethod.POST)
    Page<T> searchByExample(@RequestBody QueryExample<T> pageQuery);

    @RequestMapping(value = "/_search_by_ids", method = RequestMethod.POST)
    List<T> searchByIds(@RequestBody List<Long> ids);
}
