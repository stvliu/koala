package org.openkoala.organisation.web.controller;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.dayatang.querychannel.Page;
import org.openkoala.organisation.facade.PostFacade;
import org.openkoala.organisation.facade.dto.PostDTO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 岗位管理Controller
 *
 * @author xmfang
 */
@Controller
@RequestMapping("/post")
public class PostController extends BaseController {

    @Inject
    private PostFacade postFacade;

    /**
     * 分页查询职务
     *
     * @param page
     * @param pagesize
     * @param postDto
     * @return
     */
    @ResponseBody
    @RequestMapping("/pagingquery")
    public Page pagingQuery(int page, int pagesize, PostDTO postDto) {
    	return postFacade.pagingQueryPosts(postDto, page, pagesize);
    }

    /**
     * 创建一个岗位
     *
     * @param post
     * @return
     */
    @ResponseBody
    @RequestMapping("/create")
    public Map<String, Object> createPost(PostDTO postDTO, Long organizationId) {
        Map<String, Object> dataMap = new HashMap<String, Object>();
    	postDTO.setOrganizationId(organizationId);
        dataMap.put("result", postFacade.createPost(postDTO).getMessage());
        return dataMap;
    }

	/**
	 * 更新岗位信息
	 * 
	 * @param post
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/update")
	public Map<String, Object> updatePost(PostDTO postDTO, Long organizationId) {
		Map<String, Object> dataMap = new HashMap<String, Object>();
		postDTO.setOrganizationId(organizationId);
		dataMap.put("result", postFacade.updatePostInfo(postDTO).getMessage());
		return dataMap;
	}

    @ResponseBody
    @RequestMapping("/query-post-by-org")
    public Map<String, Object> queryPostsOfOrganization(Long organizationId) {
        Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("result", postFacade.findPostsByOrganizationId(organizationId));
        return dataMap;
    }

    @ResponseBody
    @RequestMapping("/paging-query-post-by-org")
    public Page pagingQueryPostsOfOrganization(Long organizationId, PostDTO example, int page, int pagesize) {
        return postFacade.pagingQueryPostsOfOrganizatoin(organizationId, example, page, pagesize);
    }

    /**
     * 根据ID获得职务
     *
     * @param id
     * @return
     */
    @ResponseBody
    @RequestMapping("/get/{id}")
    public Map<String, Object> get(@PathVariable("id") Long id) {
        Map<String, Object> dataMap = new HashMap<String, Object>();
        try {
            dataMap.put("data", postFacade.getPostById(id));
        } catch (Exception e) {
            dataMap.put("error", "查询指定岗位失败！");
            e.printStackTrace();
        }
        return dataMap;
    }

    /**
     * 撤销某个职务
     *
     * @param postDto
     * @return
     */
    @ResponseBody
    @RequestMapping("/terminate")
    public Map<String, Object> terminatePost(PostDTO postDTO) {
        Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("result", postFacade.terminatePost(postDTO).getMessage());
        return dataMap;
    }

    /**
     * 同时撤销多个职务
     *
     * @param postDTOs
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/terminate-posts", method = RequestMethod.POST, consumes = "application/json")
    public Map<String, Object> terminatePosts(@RequestBody PostDTO[] postDTOs) {
        Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("result", postFacade.terminatePosts(postDTOs));
        return dataMap;
    }
}