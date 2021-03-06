package ink.helloworld.halo.web.controller.admin;

import ink.helloworld.halo.model.domain.Post;
import ink.helloworld.halo.model.domain.User;
import ink.helloworld.halo.model.dto.JsonResult;
import ink.helloworld.halo.model.dto.LogsRecord;
import ink.helloworld.halo.model.enums.BlogPropertiesEnum;
import ink.helloworld.halo.model.enums.PostStatusEnum;
import ink.helloworld.halo.model.enums.PostTypeEnum;
import ink.helloworld.halo.model.enums.ResultCodeEnum;
import ink.helloworld.halo.service.LogsService;
import ink.helloworld.halo.service.PostService;
import ink.helloworld.halo.utils.HaloUtils;
import ink.helloworld.halo.utils.LocaleMessageUtil;
import ink.helloworld.halo.utils.MarkdownUtils;
import ink.helloworld.halo.web.controller.core.BaseController;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static ink.helloworld.halo.model.dto.HaloConst.OPTIONS;
import static ink.helloworld.halo.model.dto.HaloConst.USER_SESSION_KEY;
import static org.springframework.data.domain.Sort.Direction.DESC;

/**
 * <pre>
 *     后台文章管理控制器
 * </pre>
 *
 * @author : RYAN0UP
 * @date : 2017/12/10
 */
@Slf4j
@Controller
@RequestMapping(value = "/admin/posts")
public class PostController extends BaseController {

    @Autowired
    private PostService postService;

    @Autowired
    private LogsService logsService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private LocaleMessageUtil localeMessageUtil;

    /**
     * 去除html，htm后缀，以及将空格替换成-
     *
     * @param url url
     * @return String
     */
    private static String urlFilter(String url) {
        if (null != url) {
            final boolean urlEndsWithHtmlPostFix = url.endsWith(".html") || url.endsWith(".htm");
            if (urlEndsWithHtmlPostFix) {
                return url.substring(0, url.lastIndexOf("."));
            }
        }
        return StrUtil.replace(url, " ", "-");
    }

    /**
     * 处理后台获取文章列表的请求
     *
     * @param model model
     * @return 模板路径admin/admin_post
     */
    @GetMapping
    public String posts(Model model,
                        @RequestParam(value = "status", defaultValue = "0") Integer status,
                        @PageableDefault(sort = "postDate", direction = DESC) Pageable pageable) {
        final Page<Post> posts = postService.findPostByStatus(status, PostTypeEnum.POST_TYPE_POST.getDesc(), pageable);
        model.addAttribute("posts", posts);
        model.addAttribute("publishCount", postService.getCountByStatus(PostStatusEnum.PUBLISHED.getCode()));
        model.addAttribute("draftCount", postService.getCountByStatus(PostStatusEnum.DRAFT.getCode()));
        model.addAttribute("trashCount", postService.getCountByStatus(PostStatusEnum.RECYCLE.getCode()));
        model.addAttribute("status", status);
        return "admin/admin_post";
    }

    /**
     * 处理跳转到新建文章页面
     *
     * @return 模板路径admin/admin_editor
     */
    @GetMapping(value = "/write")
    public String writePost() {
        return "admin/admin_post_new";
    }

    /**
     * 跳转到编辑文章页面
     *
     * @param postId 文章编号
     * @param model  model
     * @return 模板路径admin/admin_editor
     */
    @GetMapping(value = "/edit")
    public String editPost(@RequestParam("postId") Long postId, Model model) {
        final Optional<Post> post = postService.fetchById(postId);
        model.addAttribute("post", post.orElse(new Post()));
        return "admin/admin_post_edit";
    }

    /**
     * 添加文章
     *
     * @param post     post
     * @param cateList 分类列表
     * @param tagList  标签
     * @param session  session
     */
    @PostMapping(value = "/save")
    @ResponseBody
    public JsonResult save(@ModelAttribute Post post,
                           @RequestParam("cateList") List<String> cateList,
                           @RequestParam("tagList") String tagList,
//                           @RequestParam("metas") List<PostMeta> metas,
                           HttpSession session) {
//        post.setPostMetas(metas);
        final User user = (User) session.getAttribute(USER_SESSION_KEY);
        try {
            post.setPostContent(MarkdownUtils.renderMarkdown(post.getPostContentMd()));
            post.setUser(user);
            post = postService.buildCategoriesAndTags(post, cateList, tagList);
            post.setPostUrl(urlFilter(post.getPostUrl()));
            if (StrUtil.isNotEmpty(post.getPostPassword())) {
                post.setPostPassword(SecureUtil.md5(post.getPostPassword()));
            }
            //当没有选择文章缩略图的时候，自动分配一张内置的缩略图
            if (StrUtil.equals(post.getPostThumbnail(), BlogPropertiesEnum.DEFAULT_THUMBNAIL.getProp())) {
                post.setPostThumbnail(OPTIONS.get(BlogPropertiesEnum.BLOG_URL.getProp()) + "/static/halo-frontend/images/thumbnail/thumbnail-" + RandomUtil.randomInt(1, 11) + ".jpg");
            }
            postService.create(post);
            logsService.save(LogsRecord.PUSH_POST, post.getPostTitle(), request);
            return new JsonResult(ResultCodeEnum.SUCCESS.getCode(), localeMessageUtil.getMessage("code.admin.common.save-success"));
        } catch (Exception e) {
            log.error("Save article failed: {}", e.getMessage());
            e.printStackTrace();
            return new JsonResult(ResultCodeEnum.FAIL.getCode(), localeMessageUtil.getMessage("code.admin.common.save-failed"));
        }
    }

    /**
     * 更新
     *
     * @param post     post
     * @param cateList 分类目录
     * @param tagList  标签
     * @return JsonResult
     */
    @PostMapping(value = "/update")
    @ResponseBody
    public JsonResult update(@ModelAttribute Post post,
                             @RequestParam("cateList") List<String> cateList,
                             @RequestParam("tagList") String tagList) {
        //old data
        final Post oldPost = postService.fetchById(post.getPostId()).orElse(new Post());
        post.setPostViews(oldPost.getPostViews());
        post.setPostContent(MarkdownUtils.renderMarkdown(post.getPostContentMd()));
        post.setUser(oldPost.getUser());
        if (null == post.getPostDate()) {
            post.setPostDate(new Date());
        }
        post = postService.buildCategoriesAndTags(post, cateList, tagList);
        if (StrUtil.isNotEmpty(post.getPostPassword())) {
            post.setPostPassword(SecureUtil.md5(post.getPostPassword()));
        }
        //当没有选择文章缩略图的时候，自动分配一张内置的缩略图
        if (StrUtil.equals(post.getPostThumbnail(), BlogPropertiesEnum.DEFAULT_THUMBNAIL.getProp())) {
            post.setPostThumbnail(OPTIONS.get(BlogPropertiesEnum.BLOG_URL.getProp()) + "/static/halo-frontend/images/thumbnail/thumbnail-" + RandomUtil.randomInt(1, 11) + ".jpg");
        }
        post = postService.create(post);
        if (null != post) {
            return new JsonResult(ResultCodeEnum.SUCCESS.getCode(), localeMessageUtil.getMessage("code.admin.common.update-success"));
        } else {
            return new JsonResult(ResultCodeEnum.FAIL.getCode(), localeMessageUtil.getMessage("code.admin.common.update-failed"));
        }
    }

    /**
     * 处理移至回收站的请求
     *
     * @param postId 文章编号
     * @return 重定向到/admin/posts
     */
    @GetMapping(value = "/throw")
    public String moveToTrash(@RequestParam("postId") Long postId, @RequestParam("status") Integer status) {
        try {
            postService.updatePostStatus(postId, PostStatusEnum.RECYCLE.getCode());
            log.info("Article number {} has been moved to the recycle bin", postId);
        } catch (Exception e) {
            log.error("Deleting article to recycle bin failed: {}", e.getMessage());
        }
        return "redirect:/admin/posts?status=" + status;
    }

    /**
     * 处理文章为发布的状态
     *
     * @param postId 文章编号
     * @return 重定向到/admin/posts
     */
    @GetMapping(value = "/revert")
    public String moveToPublish(@RequestParam("postId") Long postId,
                                @RequestParam("status") Integer status) {
        try {
            postService.updatePostStatus(postId, PostStatusEnum.PUBLISHED.getCode());
            log.info("Article number {} has been changed to release status", postId);
        } catch (Exception e) {
            log.error("Publishing article failed: {}", e.getMessage());
        }
        return "redirect:/admin/posts?status=" + status;
    }

    /**
     * 处理删除文章的请求
     *
     * @param postId 文章编号
     * @return 重定向到/admin/posts
     */
    @GetMapping(value = "/remove")
    public String removePost(@RequestParam("postId") Long postId, @RequestParam("postType") String postType) {
        try {
            final Optional<Post> post = postService.fetchById(postId);
            postService.removeById(postId);
            logsService.save(LogsRecord.REMOVE_POST, post.get().getPostTitle(), request);
        } catch (Exception e) {
            log.error("Delete article failed: {}", e.getMessage());
        }
        if (StrUtil.equals(PostTypeEnum.POST_TYPE_POST.getDesc(), postType)) {
            return "redirect:/admin/posts?status=2";
        }
        return "redirect:/admin/page";
    }

    /**
     * 更新所有摘要
     *
     * @param postSummary 文章摘要字数
     * @return JsonResult
     */
    @GetMapping(value = "/updateSummary")
    @ResponseBody
    public JsonResult updateSummary(@RequestParam("postSummary") Integer postSummary) {
        try {
            postService.updateAllSummary(postSummary);
        } catch (Exception e) {
            log.error("Update summary failed: {}", e.getMessage());
            e.printStackTrace();
            return new JsonResult(ResultCodeEnum.FAIL.getCode(), localeMessageUtil.getMessage("code.admin.common.update-failed"));
        }
        return new JsonResult(ResultCodeEnum.SUCCESS.getCode(), localeMessageUtil.getMessage("code.admin.common.update-success"));
    }

    /**
     * 验证文章路径是否已经存在
     *
     * @param postUrl 文章路径
     * @return JsonResult
     */
    @GetMapping(value = "/checkUrl")
    @ResponseBody
    public JsonResult checkUrlExists(@RequestParam("postUrl") String postUrl) {
        postUrl = urlFilter(postUrl);
        final Post post = postService.findByPostUrl(postUrl, PostTypeEnum.POST_TYPE_POST.getDesc());
        if (null != post) {
            return new JsonResult(ResultCodeEnum.FAIL.getCode(), localeMessageUtil.getMessage("code.admin.common.url-is-exists"));
        }
        return new JsonResult(ResultCodeEnum.SUCCESS.getCode(), "");
    }

    /**
     * 将所有文章推送到百度
     *
     * @param baiduToken baiduToken
     * @return JsonResult
     */
    @GetMapping(value = "/pushAllToBaidu")
    @ResponseBody
    public JsonResult pushAllToBaidu(@RequestParam("baiduToken") String baiduToken) {
        if (StrUtil.isBlank(baiduToken)) {
            return new JsonResult(ResultCodeEnum.FAIL.getCode(), localeMessageUtil.getMessage("code.admin.post.no-baidu-token"));
        }
        final String blogUrl = OPTIONS.get(BlogPropertiesEnum.BLOG_URL.getProp());
        final List<Post> posts = postService.findAll(PostTypeEnum.POST_TYPE_POST.getDesc());
        final StringBuilder urls = new StringBuilder();
        for (Post post : posts) {
            urls.append(blogUrl);
            urls.append("/archives/");
            urls.append(post.getPostUrl());
            urls.append("\n");
        }
        final String result = HaloUtils.baiduPost(blogUrl, baiduToken, urls.toString());
        if (StrUtil.isEmpty(result)) {
            return new JsonResult(ResultCodeEnum.FAIL.getCode(), localeMessageUtil.getMessage("code.admin.post.push-to-baidu-failed"));
        }
        return new JsonResult(ResultCodeEnum.SUCCESS.getCode(), localeMessageUtil.getMessage("code.admin.post.push-to-baidu-success"));
    }

    @InitBinder
    public void initBinder(ServletRequestDataBinder binder) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(sdf, true));
    }
}
