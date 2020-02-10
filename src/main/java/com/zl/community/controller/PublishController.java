package com.zl.community.controller;

import com.zl.community.mapper.QuestionMapper;
import com.zl.community.mapper.UserMapper;
import com.zl.community.model.Question;
import com.zl.community.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@Controller
public class PublishController {

    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private UserMapper userMapper;

//    @GetMapping("/publish/{id}")
//    public String edit(@PathVariable(name = "id") Long id,
//                       Model model) {
//        QuestionDTO question = questionService.getById(id);
//        model.addAttribute("title", question.getTitle());
//        model.addAttribute("description", question.getDescription());
//        model.addAttribute("tag", question.getTag());
//        model.addAttribute("id", question.getId());
//        model.addAttribute("tags", TagCache.get());
//        return "publish";
//    }


    @GetMapping("/publish")
    public String publish(Model model) {
//        model.addAttribute("tags", TagCache.get());
        return "publish";
    }

    @PostMapping("/publish")
    public String doPublish(
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "tag", required = false) String tag,
            @RequestParam(value = "id", required = false) Long id,
            HttpServletRequest request,
            Model model) {
        model.addAttribute("title", title);
        model.addAttribute("description", description);
        model.addAttribute("tag", tag);
//        model.addAttribute("tags", TagCache.get());

        if(title==null ||title.equals("")){
            model.addAttribute("error","标题不能为空");
            return "publish";
        }

        if(description==null ||description.equals("")){
            model.addAttribute("error","问题补充不能为空");
            return "publish";
        }

        if(tag==null ||tag.equals("")){
            model.addAttribute("error","标签不能为空");
            return "publish";
        }
//
//        if (StringUtils.isBlank(title)) {
//            model.addAttribute("error", "标题不能为空");
//            return "publish";
//        }
//        if (StringUtils.isBlank(description)) {
//            model.addAttribute("error", "问题补充不能为空");
//            return "publish";
//        }
//        if (StringUtils.isBlank(tag)) {
//            model.addAttribute("error", "标签不能为空");
//            return "publish";
//        }
//
//        String invalid = TagCache.filterInvalid(tag);
//        if (StringUtils.isNotBlank(invalid)) {
//            model.addAttribute("error", "输入非法标签:" + invalid);
//            return "publish";
//        }
//
//        User user = (User) request.getSession().getAttribute("user");
//        if (user == null) {
//            model.addAttribute("error", "用户未登录");
//            return "publish";
//        }
        User user = null;
        Cookie[] cookies = request.getCookies();
        for(Cookie cookie:cookies){
            if(cookie.getName().equals("token")){
                String token = cookie.getValue();
                user = userMapper.findByToken(token);
                if(user!=null){
                    request.getSession().setAttribute("user",user);
                }
                break;
            }
        }
        if( user == null){
            model.addAttribute("error","用户未登录");
            return "publish";
        }

        Question question = new Question();
        question.setTitle(title);
        question.setDescription(description);
        question.setTag(tag);
        question.setCreator(user.getId());
        question.setGmtCreate(System.currentTimeMillis());
        question.setGmtModified(question.getGmtCreate());
//        question.setId(id);
//        questionService.createOrUpdate(question);

        questionMapper.create(question);
        return "redirect:/";
    }
}
