package com.cm.cloud.commons.db.controller;

import com.cm.cloud.commons.db.CommonMapper;
import com.cm.cloud.commons.enums.EnumQueryMatch;
import com.cm.cloud.commons.enums.EnumQueryOrder;
import com.cm.cloud.commons.excption.ServiceLogicException;
import com.cm.cloud.commons.pojo.dto.BatchUpdateDTO;
import com.cm.cloud.commons.pojo.dto.PageQuery;
import com.cm.cloud.commons.pojo.dto.QueryExample;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import tk.mybatis.mapper.entity.Example;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public abstract class BasicController<T> /*implements BasicClient<T>*/ {

    private CommonMapper<T> commonMapper;

    private Class<T> clazz;

    @Autowired
    ObjectMapper objectMapper;

    public BasicController(CommonMapper<T> commonMapper) {

        this.commonMapper = commonMapper;

        Type genericSuperclass = this.getClass().getGenericSuperclass();
        if (genericSuperclass instanceof ParameterizedType) {
            clazz = (Class<T>) ((ParameterizedType) genericSuperclass).getActualTypeArguments()[0];
        }
    }

    /**
     * 新增
     *
     * @return
     */
//    @Override
    @RequestMapping(value = "", method = RequestMethod.POST)
    public T save(@RequestBody T model) {
        commonMapper.insertUseGeneratedKeys(model);

        return model;
    }

    @RequestMapping(value = "/_batch_add", method = RequestMethod.POST)
    public List<T> saveList(@RequestBody List<T> model) {
        commonMapper.insertList(model);
        return model;
    }

    @RequestMapping(value = "/_batch_update", method = RequestMethod.POST)
    public int batchUpdate(@RequestBody BatchUpdateDTO<T> dto) {

        Example example = new Example(dto.getModel().getClass());
        example.createCriteria().andIn("id", dto.getIds());
        int rows = commonMapper.updateByExampleSelective(dto.getModel(), example);
        if (rows != dto.getIds().size()) {
            throw new ServiceLogicException("批处理修改成功行数与id集size不等");
        }
        return rows;
    }

    /**
     * 全量修改
     *
     * @param mode
     * @return
     */
//    @Override
    @RequestMapping(value = "", method = RequestMethod.PUT)
    public T update(@RequestBody T mode) {
        commonMapper.updateByPrimaryKey(mode);
        return mode;
    }

    /**
     * 局部修改
     *
     * @param model
     * @return
     */
//    @Override
    @RequestMapping(value = "", method = RequestMethod.PATCH)
    public T updateSelective(@RequestBody T model) {
        commonMapper.updateByPrimaryKeySelective(model);
        return model;
    }

    //    @Override
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public boolean delete(@PathVariable("id") Long id) {
        return commonMapper.deleteByPrimaryKey(id) == 1;
    }

    //    @Override
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public T loadById(@PathVariable("id") Long id) {
        return commonMapper.selectByPrimaryKey(id);
    }

    /**
     * 单行查询
     *
     * @param model
     * @return
     */
//    @Override
    @RequestMapping(value = "/_search_one", method = RequestMethod.POST)
    public T loadOne(@RequestBody T model) {
        return commonMapper.selectOne(model);
    }

    //    @Override
    @RequestMapping(value = "/_search", method = RequestMethod.POST)
    public List<T> loadList(@RequestBody T model) {
        return commonMapper.select(model);
    }

    //    @Override
    @RequestMapping(value = "/_search_page", method = RequestMethod.POST)
    public PageInfo<T> loadPage(@RequestBody PageQuery<T> query) {
        return PageHelper.startPage(query.getPageNum(), query.getPageSize()).doSelectPageInfo(
                () -> commonMapper.select(query.getQueryPo()));
    }

    /**
     * 自定义查询
     *
     * @param pageQuery
     * @return
     */
    @RequestMapping(value = "/_search_by_example", method = RequestMethod.POST)
    public Page<T> searchByExample(@RequestBody QueryExample<T> pageQuery) {

        Map<String, Object> map = objectMapper.convertValue(pageQuery.getQueryPo(), Map.class);
//        JSONObject map = (JSONObject) JSONObject.toJSON(pageQuery.getQueryPo());
        Example example = new Example(clazz);
        if (!map.isEmpty()) {
            Example.Criteria criteria = example.createCriteria();

            map.forEach((property, value) -> {

                if (Objects.isNull(value))
                    return;
                switch (queryMatch(pageQuery.getMatch(), property, value)) {
                    case EQ:
                        criteria.andEqualTo(property, value);
                        break;
                    case GT:
                        criteria.andGreaterThan(property, value);
                        break;
                    case LT:
                        criteria.andLessThan(property, value);
                        break;
                    case GTE:
                        criteria.andGreaterThanOrEqualTo(property, value);
                        break;
                    case LTE:
                        criteria.andLessThanOrEqualTo(property, value);
                        break;
                    case LIKE:
                        criteria.andLike(property, (String) value);
                        break;
                }
            });
        }

        Map<String, String> order = pageQuery.getOrder();

        if (!(Objects.isNull(order) || order.isEmpty())) {
            order.forEach((property, orderType) -> {
                switch (EnumQueryOrder.toEnum(orderType)) {
                    case ASC:
                        example.orderBy(property).asc();
                        break;
                    case DESC:
                        example.orderBy(property).desc();
                        break;
                }
            });
        }

        QueryExample.PageInfo pageInfo = pageQuery.getPageInfo();

        if (!CollectionUtils.isEmpty(pageQuery.getSelectProperties())) {

            example.selectProperties(pageQuery.getSelectProperties().toArray(new String[]{}));
        }

        if (!CollectionUtils.isEmpty(pageQuery.getExcludeProperties())) {
            example.excludeProperties(pageQuery.getExcludeProperties().toArray(new String[]{}));
        }
        if (Objects.nonNull(pageInfo)) {
            Page<T> result = PageHelper.startPage(pageInfo.getPageNum(), pageInfo.getPageSize())
                    .count(pageInfo.getCount()).doSelectPage(() -> commonMapper.selectByExample(example));
            if (Objects.nonNull(pageInfo.getTotal()))
                result.setTotal(pageInfo.getTotal());
            return result;
        }

        List<T> games = commonMapper.selectByExample(example);
        Page<T> page = new Page<>();
        page.addAll(games);
        return page;

    }

    private EnumQueryMatch queryMatch(Map<String, String> match, String property, Object value) {

        if (Objects.nonNull(value)) {
            if (Objects.nonNull(match)) {
                if (match.containsKey(property)) {
                    return EnumQueryMatch.toEnum(match.get(property));
                }
            }
        }
        return EnumQueryMatch.EQ;
    }

    /**
     * count 行数
     *
     * @param model
     * @return
     */
    @RequestMapping(value = "/_load_count", method = RequestMethod.POST)
    public int loadCount(T model) {
        return commonMapper.selectCount(model);
    }

    @RequestMapping(value = "/_search_by_ids", method = RequestMethod.POST)
    public List<T> searchByIds(@RequestBody List<Long> ids){

        Example example = new Example(clazz);
        example.createCriteria().andIn("id",ids);
        return commonMapper.selectByExample(example);
    }
}
