package com.jqh.forum.qa.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.jqh.forum.qa.mapper.ProblemMapper;
import com.jqh.forum.qa.mapper.ReplyMapper;
import com.jqh.forum.qa.pojo.Reply;
import entity.PageResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
import util.IdWorker;
import com.jqh.forum.qa.pojo.Problem;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 服务层
 *
 * @author Administrator
 */
@Service
@Transactional
public class ProblemService {

    @Resource
    private ProblemMapper problemMapper;
    @Resource
    private ReplyMapper replyMapper;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private HttpServletRequest request;

    /**
     * 查询全部列表
     *
     * @return
     */
    public List<Problem> findAll() {
        return problemMapper.selectAll();
    }


    /**
     * 条件查询+分页
     *
     * @param whereMap
     * @param page
     * @param size
     * @return
     */
    public PageResult<Problem> findSearch(Map whereMap, int page, int size) {
        Page<Problem> problems = PageHelper.startPage(page, size);
        List<Problem> search = this.findSearch(whereMap);
        PageResult<Problem> problemPageResult = new PageResult<>(problems.getTotal(), search);
        return problemPageResult;
    }


    /**
     * 条件查询
     *
     * @param searchMap
     * @return
     */
    public List<Problem> findSearch(Map searchMap) {
        Example example = new Example(Problem.class);
        Example.Criteria criteria = example.createCriteria();
        // ID
        if (searchMap.get("id") != null && !"".equals(searchMap.get("id"))) {
            criteria.andLike("id", "%" + (String) searchMap.get("id") + "%");
        }
        // 标题
        if (searchMap.get("title") != null && !"".equals(searchMap.get("title"))) {
            criteria.andLike("title", "%" + (String) searchMap.get("title") + "%");
        }
        // 内容
        if (searchMap.get("content") != null && !"".equals(searchMap.get("content"))) {
            criteria.andLike("content", "%" + (String) searchMap.get("content") + "%");
        }
        // 用户ID
        if (searchMap.get("userid") != null && !"".equals(searchMap.get("userid"))) {
            criteria.andLike("userid", "%" + (String) searchMap.get("userid") + "%");
        }
        // 昵称
        if (searchMap.get("nickname") != null && !"".equals(searchMap.get("nickname"))) {
            criteria.andLike("nickname", "%" + (String) searchMap.get("nickname") + "%");
        }
        // 是否解决
        if (searchMap.get("solve") != null && !"".equals(searchMap.get("solve"))) {
            criteria.andLike("solve", "%" + (String) searchMap.get("solve") + "%");
        }
        // 回复人昵称
        if (searchMap.get("replyname") != null && !"".equals(searchMap.get("replyname"))) {
            criteria.andLike("replyname", "%" + (String) searchMap.get("replyname") + "%");
        }
        return problemMapper.selectByExample(example);
    }

    /**
     * 根据ID查询实体
     *
     * @param id
     * @return
     */
    public Problem findById(String id) {
        return problemMapper.selectByPrimaryKey(id);
    }

    /**
     * 增加
     *
     * @param problem
     */
    public void add(Problem problem) {
        String token = (String) request.getAttribute("claims_user");
        if (StringUtils.isBlank(token)) {
            throw new RuntimeException("权限不足");
        }
        problem.setId(idWorker.nextId() + "");
        problem.setUpdatetime(new Date());
        problem.setCreatetime(new Date());
        problem.setReply((long)0);
        problem.setThumbup((long)0);
        problem.setSolve("0");
        problem.setVisits((long)0);
        problemMapper.insert(problem);
    }

    /**
     * 修改
     *
     * @param problem
     */
    public void update(Problem problem) {
        problemMapper.updateByPrimaryKey(problem);
    }

    /**
     * 删除
     *
     * @param id
     */
    public void deleteById(String id) {
        problemMapper.deleteByPrimaryKey(id);
    }

    //最近被回复
    public PageResult<Problem> newlist(String lableid, int page, int size) {
        Page<Problem> problems = PageHelper.startPage(page, size);
        List<Problem> newlist = problemMapper.newlist(lableid);
        return new PageResult(problems.getTotal(), newlist);
    }

    //最多回复
    public PageResult<Problem> hotlist(String lableid, int page, int size) {
        Page<Problem> problems = PageHelper.startPage(page, size);
        List<Problem> newlist = problemMapper.hotlist(lableid);
        return new PageResult(problems.getTotal(), newlist);
    }

    //待回复
    public PageResult<Problem> waitlist(String lableid, int page, int size) {
        Page<Problem> problems = PageHelper.startPage(page, size);
        List<Problem> newlist = problemMapper.waitlist(lableid);
        return new PageResult(problems.getTotal(), newlist);
    }

    /**
     * 设置某个问题为已解决
     *
     * @param problemId
     * @param replyId
     */
    public void solve(String problemId, String replyId) {
        Problem problem = problemMapper.selectByPrimaryKey(problemId);
        Reply reply = replyMapper.selectByPrimaryKey(replyId);
        problem.setSolve("1");
        problem.setReplyid(replyId);
        problem.setUpdatetime(new Date());
        problem.setReplyname(reply.getNickname());
        problem.setReplytime(reply.getCreatetime());
        problemMapper.updateByPrimaryKeySelective(problem);
    }

    /**
     * 点赞
     * @param problemId
     */
    public void addThumbup(String problemId) {
        problemMapper.addThumbup(problemId);
    }
}
