/**
 * Created by Administrator on 2015/11/15.
 */

var PIC_SIZE = 2*1024 * 1024;
var page_index = 1;
var opts = {
    lines: 13, // 花瓣数目
    length: 7, // 花瓣长度
    width: 4, // 花瓣宽度
    radius: 10, // 花瓣距中心半径
    corners: 1, // 花瓣圆滑度 (0-1)
    rotate: 0, // 花瓣旋转角度
    direction: 1, // 花瓣旋转方向 1: 顺时针, -1: 逆时针
    color: '#5882FA', // 花瓣颜色
    speed: 1, // 花瓣旋转速度
    trail: 60, // 花瓣旋转时的拖影(百分比)
    shadow: false, // 花瓣是否显示阴影
    hwaccel: false, //spinner 是否启用硬件加速及高速旋转
    className: 'spinner', // spinner css 样式名称
    zIndex: 2e9, // spinner的z轴 (默认是2000000000)
    top: 'auto', // spinner 相对父容器Top定位 单位 px
    left: 'auto'// spinner 相对父容器Left定位 单位 px
};
var spinner = new Spinner(opts);
var mod = "0";
//var target = $(".spin").get(0);
//spinner.spin(target);
$(function () {
    Date.prototype.format = function (fmt) { //author: meizz
        var o = {
            "M+": this.getMonth() + 1, //月份
            "d+": this.getDate(), //日
            "h+": this.getHours(), //小时
            "m+": this.getMinutes(), //分
            "s+": this.getSeconds(), //秒
            "q+": Math.floor((this.getMonth() + 3) / 3), //季度
            "S": this.getMilliseconds() //毫秒
        };
        if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
        for (var k in o)
            if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
        return fmt;
    }
    initButton();
    fetchData(0);
});
function initButton() {
    //点击选择图片时下拉出现的模板
    $("#camera-chooser").on("click", function () {
        showPicture();
    });
    //关闭模板时模板上滑消失
    $("#img-close-icon").on("click", function () {
        hidePicture();
    });
    //删除选中的文件和缩略图
    $("#img-remove-icon").on("click", function () {
        $("#picture").val("");
        $("#show-picture").css("display", "none");
        $(".upload-image").css("display", "none");
        $("#img-remove-icon").css("display", "none");
    });
    //更改上传的图片时的业务逻辑
    $("#picture").on("change", function () {
        var pictureform = $("#picture")[0];
        var picturepath = pictureform.value;
        var src = window.URL.createObjectURL(pictureform.files[0]);
        var reg = /\.(jpeg|jpg|gif|png|ico|bmp)$/i;
        if (!reg.test(picturepath)) {
            $("#file-size-warning").css("color", "#E84940");
            $("#file-size-warning").html("你上传的文件非图片！我要告诉管理员！");
            $("#picture").val("");
        } else if (pictureform.files[0].size > PIC_SIZE) {
            $("#file-size-warning").css("color", "#E84940");
            $("#file-size-warning").html("你上传的图片太大了！我要告诉管理员！");
            $("#picture").val("");
        } else {
            $("#file-size-warning").css("color", "#999999");
            $("#file-size-warning").html("请选择图片文件，2M是最大的限制了~~");
            $("#show-picture").css("display", "inline-block");
            //设置图片地址 并显示图片
            $(".upload-image").attr("src", src);
            $(".upload-image").css("display", "block");
            //显示删除按钮
            $("#img-remove-icon").css("display", "block");
        }
    });

    $(".item-box").on("click", function () {
        $(".item-box").removeClass("strategy-selected");
        $(this).addClass("strategy-selected");
        mod = $(this).attr("mod");
        clearData();
        fetchData(parseInt(mod));
    });
    $("#announce-remove").click(function () {
        $("#announce-board").slideUp(200, function () {
            $(this).css("display", "none");
        });
    });

}
//设置提示
function setTitle(){
    $(".share-btn").attr("title", "转发数");
    $(".cmt-btn").attr("title", "评论数");
    $(".agree-btn").attr("title", "获赞数");
    $(".delete-btn").attr("title", "删除");
}
//提交一条动态
function submitAction(){
    var pictureform = $("#picture")[0]
    var formData = new FormData();
    if(pictureform.value!=""){
        formData.append("picture",pictureform.files[0]);
    }
    if ($("#trend-message-body").val()== "") {
        alert("请填写内容！")
    } else {
        formData.append("content",$("#trend-message-body").val());
        $.ajax({
            method:"POST",
            processData : false,
            contentType : false,
            data : formData,
            type:"POST",
            url:"activity/"+current_login_user.clientId+"/add",
            success:function(data){
                $("#trend-message-body").val("");
                initPicture();
                if(data.code==404){
                    alert(data.message);
                }else{
                    prependActivity(data.activity,data.sys_time);
                    $("#ModalLabel").html("<h3 >^_^ 您已成功发表一条动态</h3>");
                    showModal();
                }
                //刷新面板上的四个按钮
                flushPanelButton();
            }
        });
    }
}
/**
 * 图片选择控件的初始化
 */
function initPicture(){
    $("#file-size-warning").css("color", "#999999");
    $("#file-size-warning").html("请选择图片文件，2M是最大的限制了~~");
    clearPicture();
}
function clearPicture(){
    $("#picture").val("");
    $("#show-picture").css("display", "none");
    $(".upload-image").css("display", "none");
    $("#img-remove-icon").css("display", "none");
    $('#image-board').slideUp(200, function () {
        $(this).css("display", "none");
    });
}
function hidePicture(){
    $('#image-board').slideUp(200, function () {
        $(this).css("display", "none");
    });
}
function showPicture(){
    $('#image-board').slideDown(200, function () {
        $(this).css("display", "block");
    })
}
/**
 * 清除全部动态数据
 */
function clearData(){
    $("#contents-body").children("dl").remove();
    $("#contents-body").next().remove();
}
/**
 * 评论按钮（点击下拉上拉）事件刷新
 */
function flushCommentBtn() {
    $(".cmt-btn").unbind("click");
    $(".cmt-btn").on("click",function () {
        var comments = $(this).parent().parent(".trend-message-foot").next(".comment-list");
        var activity_id = $(this).parent().parent(".trend-message-foot")
            .next(".comment-list").attr('activity_id');
        if (comments.hasClass("hidden-block")) {
            $.ajax({
                type: "GET",
                url: "comment/fetch?activity_id=" + activity_id,
                dataType: "json",
                contentType: "application/json",
                success: function (data) {
                    if (data.comments.length > 0) {
                        for(var i=0;i<data.comments.length;i++){
                            makeComment(data.comments[i],data.sys_time,activity_id);
                        }
                    }
                    //评论按钮绑定完后，绑定回复下拉按钮
                    replyBtnBind();
                }
            });
            comments.slideDown(150,function () {
                comments.removeClass("hidden-block")
                comments.addClass("show-block");
            });
        } else {
            comments.slideUp(200, function () {
                comments.removeClass("show-block")
                comments.addClass("hidden-block");
                var comment_list = $("#"+activity_id).children(":last").children()
                    .children(".comment-list");
                comment_list.children().not(".comment-input").remove();
            });
        }
    });
    setCommentButton(comment,"");
}
/**
 * 追加生成动态面板
 * @param activity
 * @param sys_time
 */
function generateActivity(activity,sys_time) {
    $("#contents-body").append(baseActivityGenerator(activity,sys_time));
}
/**
 * 前顶生成动态面板
 * @param activity
 * @param sys_time
 */
function prependActivity(activity,sys_time) {
    $("#contents-body").prepend(baseActivityGenerator(activity,sys_time));
}
/**
 * 抽取出来的生成动态面板的方法
 * @param activity
 * @param sys_time
 * @returns {*|jQuery|HTMLElement}
 */
function baseActivityGenerator(activity,sys_time){
    var dl = $("<dl id='"+activity.activityId+"'></dl>");
    dl.attr("rooter-id",activity.user.clientId);
    dl.attr("source-id",activity.activityId);
    var dt = $("<dt class='pull-left'></dt>");
    var dd = $("<dd class='pull-left'></dd>");
    var headImg = $("<img class='user-img-rectangle' src='assets/images/user/" + activity.user.clientId + "/head.jpg' />")
    dt.append(headImg);
    //动态面板
    var trends_message_box = $("<div class='trends-message-box blackboard'></div>");
    //动态头
    var trends_message_head = $("<div class='trend-message-head'></div>");
    var username_lab = $("<a href='#' class='username-lab15'></a>");
    username_lab.html(activity.user.username);
    var space = $("<span class='space-18'></span>");
    var intro = $("<span class='obscure-lab' title='" + activity.user.introduce + "'> —— " + activity.user.introduce + "</span>");
    var trends_message_body = $("<div class='trend-message-body message-board'></div>");
    var content = $("<p></p>");
    var timestamp = $("<p class='obscure-lab'></p>");
    content.html(activity.content);
    timestamp.html(judgeDate(new Date(activity.createdAt),new Date(sys_time)));
    trends_message_head.append(username_lab).append(space).append(intro);
    content.html(activity.content);
    trends_message_body.append(content);
    if(activity.type=='share'){
        var hr = $("<hr class='top-10 bottom-10'/>");
        var share_content;
        //动态被删除
        if(activity.root==null){
            dl.attr("source-id","deleted");
            dl.attr("rooter-id","deleted");
            share_content = $("<h4 class='.big-20'>该条动态被作者删除。</h4>");
            var content_img = $("<img src='assets/images/static/invalid/notfound.png' class='middle-box activity_img' />")
            trends_message_body.append(hr).append(obscure_lab).append(share_content).append(content_img);
        }else{
            var obscure_lab = $("<span class='obscure-lab'>转发来自： </span>");
            var href = $("<a href='#' class='username-lab12'>#<b>"+activity.root.user.username+"</b></a>");
            obscure_lab.append(href);
            //为每条动态标记原作者id
            dl.attr("source-id",activity.root.activityId);
            dl.attr("rooter-id",activity.root.user.clientId);
            share_content = $("<p>"+activity.root.content+"</p>");
            trends_message_body.append(hr).append(obscure_lab).append(share_content);
            if(activity.root.image==1){
                var content_img = $("<img src='assets/images/activity/"+activity.root.user.clientId+"/"+activity.root.activityId+".jpg' class='activity_img' />")
                trends_message_body.append(content_img);
            }
        }
    }
    //动态体
    if(activity.image==1){
        var content_img = $("<img src='assets/images/activity/"+activity.user.clientId+"/"+activity.activityId+".jpg' class='activity_img' />")
        trends_message_body.append(content_img);
    }
    trends_message_body.append(timestamp);
    //动态页脚
    var trends_message_foot = $("<div class='trend-message-foot'></div>");
    //赞按钮
    var agree_btn = $("<span class='agree-btn'></span>");
    //赞链接
    var agree_hook = $("<a href='javascript:void(0)'></a>");
    if(containsUser(activity.agreers,current_login_user.clientId)){
        agree_hook.addClass("clicked");
    }
    //分享按钮
    var share_btn = $("<span class='share-btn'></span>");
    //分享链接
    var share_hook = $("<a href='javascript:void(0)'></a>");
    //评论按钮
    var comment_btn = $("<span class='comment-btn'></span>");
    //评论链接
    var comment_hook = $("<a href='javascript:void(0)' class='cmt-btn'></a>");
    var agree_count = $("<b class='count'></b>");
    var share_count = $("<b class='count'></b>");
    var comment_count = $("<b class='count'></b>");
    agree_count.html(activity.agreers.length);
    share_count.html(activity.shared);
    comment_count.html(activity.cmted);
    agree_hook.append("<i class='glyphicon glyphicon-thumbs-up'></i>").append(agree_count);
    share_hook.append("<i class='glyphicon glyphicon-share-alt'></i>").append(share_count);
    comment_hook.append("<i class='glyphicon glyphicon-comment'></i>").append(comment_count);
    agree_btn.append(agree_hook);
    share_btn.append(share_hook);
    comment_btn.append(comment_hook);

    trends_message_foot.append(agree_btn).append(share_btn).append(comment_btn)
    if(activity.user.clientId==current_login_user.clientId){
        //删除按钮
        var delete_btn = $("<span class='delete-btn pull-right'></span>");
        //删除链接
        var delete_hook = $("<a href='javascript:void(0)' class='del-btn pull-right'></a>");
        delete_hook.append("<i class='glyphicon glyphicon-trash'></i>");
        delete_btn.append(delete_hook);
        trends_message_foot.append(delete_btn);
    }
    //评论列表
    var comment_list = $("<div class='comment-list bar-gray hidden-block' activity_id='" + activity.activityId + "'></div>");
    //评论框面板
    var comment_div = $("<div class='comment-input'></div>");
    var comment_submit_btn = $("<button class='cmt_sub_btn btn sign-in-btn left-10 btn-yellow btn-thin'></button>");
    comment_submit_btn.append("<i class='glyphicon glyphicon-pencil btn-icon'></i><span class='btn-txt'>评论</span>");
    var comment_text = $("<input type='text' id='input"+activity.activityId+"' class='inner-element length-8 form-control' placeholder='忍不了了，我要说几句！'/>");
    comment_div.append(comment_text).append(comment_submit_btn);
    comment_list.append(comment_div);
    trends_message_box.append(trends_message_head).append(trends_message_body)
        .append(trends_message_foot).append(comment_list);
    dd.append(trends_message_box);
    dl.append(dt).append(dd);
    return dl;
}

function fetchData(mod) {
    //首页刷新所有的动态
    $.ajax({
        type: "GET",
        url: "activity/fetchAll?pageindex=1&mod="+mod,
        dataType: "json",
        contentType: "application/json",
        beforeSend: function () {
            //异步请求时spinner出现
            $(".spin").text("");
            var target = $(".spin").get(0);
            spinner.spin(target);
        },
        success: function (data) {
            spinner.spin();
            var acts = data.activities;
            for (var i = 0; i < acts.length; i++) {
                generateActivity(acts[i],data.sys_time);
            }
            var more_info = $("<div class='more-info' onclick='nextPage()'></div>");
            if (acts.length == 0) {
                more_info.append("<span>没有更多动态</span>");
                more_info.attr("onclick", "void(0)");
            } else {
                more_info.append("<a href='javascript:void(0)'>查看更多>></a>");
            }
            $("#contents-body").after(more_info);
            setTitle();
            //刷新面板上的四个按钮
            flushPanelButton();
        }
    });
}
function nextPage() {
    page_index++;
    $.ajax({
        type: "GET",
        url: "activity/fetchAll?pageindex=" + page_index+"&mod="+mod,
        dataType: "json",
        contentType: "application/json",
        success: function (data) {
            var acts = data.activities;
            if (acts.length == 0) {
                page_index--;
                $(".more-info").html("<span>没有更多动态</span>");
                $(".more-info").attr("onclick", "void(0)");
            }
            for (var i = 0; i < acts.length; i++) {
                generateActivity(acts[i],data.sys_time);
            }
            setTitle();
            //刷新面板上的四个按钮
            flushPanelButton();
        }
    });
}
//计算时间并按照自定义格式化输出的方法
function judgePastDate(date,now) {
    //时间会有几毫秒的误差，在这里稍微控制一下
    now.setTime(now.getTime()+100);
    var gap = (now - date);
    var monthGap = Math.floor(gap / (1000 * 60 * 60 * 24 * 30));
    var dateGap = Math.floor(gap / (1000 * 60 * 60 * 24));
    var hourGap = Math.floor(gap / (1000 * 60 * 60));
    var minuGap = Math.floor(gap / (1000 * 60));
    var secondGap = Math.floor(gap / (1000));
    if (date.getFullYear() != now.getFullYear() && monthGap >= 12) {
        return date.format("yyyy-MM-dd hh:mm:ss");
    } else if (monthGap < 12 && monthGap >= 1 && date.getFullYear() == now.getFullYear()) {
        return parseInt(monthGap) + "个月前";
    } else if (dateGap >= 7 && monthGap == 0) {
        return parseInt(dateGap / 7) + "周前";
    } else if (dateGap < 7 && dateGap >= 1) {
        return parseInt(dateGap) + "天前";
    } else if (hourGap < 24 && hourGap >= 1 && dateGap == 0) {
        return parseInt(hourGap) + "小时前";
    } else if (minuGap < 60 && minuGap >= 1 && hourGap == 0) {
        return parseInt(minuGap) + "分钟前";
    } else if (secondGap < 60 & secondGap >= 0 && minuGap == 0) {
        return parseInt(secondGap) + "秒前";
    } else {
        //记录日期错误
        err = gap;
        return "日期有问题";
    }
}

function judgeDate(date,now) {
    //时间会有几毫秒的误差，在这里稍微控制一下
    now.setTime(now.getTime()+100);
    var dateGap = now.getDate() - date.getDate();
    var monthGap = now.getMonth() - date.getMonth();
    if (dateGap < 1&&monthGap==0) {
        return date.format("hh:mm");
    } else if (dateGap == 1) {
        return "昨天 " + date.format("hh:mm");
    } else if (dateGap == 2) {
        return "前天 " + date.format("hh:mm");
    } else {
        return date.format("yyyy-MM-dd hh:mm:ss");
    }
}
/**
 * 生成评论内容
 * @param comment
 */
function makeComment(comment,sys_time,act_id) {
    var dl = $("<dl id='"+comment.commentId+"'></dl>");
    var dt = $("<dt class='pull-left'></dt>");
    dt.append("<img class='user-img-circle' src='assets/images/user/" + comment.user.clientId + "/head.jpg'/>")
    var dd = $("<dd class='left-50'></dd>");
    var username_lab = $("<a href='javascript:void(0)' class='username-lab12'>" + comment.user.username + " :</a>");
    var span = $("<p></p>");
    var replied = $("<span></span>");
    var slide = $("<span></span>");
    //绑定回复按钮事件
    var replied_btn = $("<a href='javascript:replyBtnEffect(\""+act_id+"\",\""+comment.commentId+"\",\""+comment.user.username+"\",\""
                        +comment.user.clientId+"\")' class='obscure-lab blod-big'>回复(<b class='blod-big'>"+comment.replied+"</b>)</a>")
    replied.append(replied_btn);
    if(comment.replied>0){
        var slide_btn = $("<a href='javascript:void(0)' class='obscure-lab blod-big slide-btn left-10'>下拉</a>")
        replied.append(slide_btn);
    }
    span.html(comment.content);
    var timestamp = $("<p class='small-12 label-gray'>" + judgePastDate(new Date(comment.commentAt),new Date(sys_time)) + "</p>");
    //回复面板
    var reply_div = $("<div class='hidden-block reply-list left-50 hidden-block'></div>")
    dd.append(username_lab).append(span).append(replied).append(timestamp);
    dl.append(dt).append(dd).append(reply_div);
    //查找哪个动态下面的评论框
    var comment_input = $("#"+act_id).children(":last").children().children(".comment-list").children(".comment-input");
    comment_input.before(dl);
}

function makeReply(reply,sys_time,comment_id){
    var dl = $("<dl id='"+reply.replyId+"' class='block-border'></dl>");
    var dt = $("<dt class='pull-left'></dt>");
    dt.append("<img class='user-img-circle' src='assets/images/user/" + reply.user.clientId + "/head.jpg'/>")
    var dd = $("<dd class='left-50'></dd>");
    var username_lab = $("<a href='javascript:void(0)' class='username-lab12'>" + reply.user.username + " </a>" +
    ":<a href='javascript:void(0)' class='username-lab12'>@"+reply.toUser.username+"</a>");
    var span = $("<p></p>");
    var replied = $("<span></span>");
    var replied_btn = $("<a href='javascript:replyBtnEffect(\""+reply.comment.activity.activityId+"\",\""+comment_id+"\",\""
            +reply.user.username+"\",\""+reply.user.clientId+"\")' class='obscure-lab blod-big'>回复</a>")
    replied.append(replied_btn);
    span.html(reply.content);
    var timestamp = $("<p class='small-12 label-gray'>" + judgePastDate(new Date(reply.replyAt),new Date(sys_time)) + "</p>");
    dd.append(username_lab).append(span).append(replied).append(timestamp);
    dl.append(dt).append(dd);
    $("#"+comment_id).children(".reply-list").append(dl);
}
function showModal(){
    $("#addModal").modal("show");
}
function hideModal(){
    $("#addModal").modal("hide");
    $("#ModalLabel").html("");
}
/**
 * 对文章的评论 事件的处理
 */
function comment() {
    var activity_id = $(this).parent().parent('.comment-list').attr("activity_id");
    var content_txt = $(this).prev().val();
    //获取评论个数面板
    var cmt_count = $("#"+activity_id).children(":last").children().children(".comment-list")
        .prev().children('.comment-btn').children().children("b");
    if(content_txt!=""){
        $.ajax({
            type: "POST",
            url: "comment/"+current_login_user.clientId+"/add/"+ activity_id ,
            dataType: "json",
            contentType: "application/json",
            data:JSON.stringify({content:content_txt}),
            success: function (data) {
                if(data.code==404){
                    alert(data.message);
                }else {
                    makeComment(data.comment,data.sys_time,activity_id);
                }
                cmt_count.text(parseInt(cmt_count.text())+1);
            }
        });
    }else{
        alert("请输入评论信息");
    }
    $(this).prev().val("");
    return this;
}
/**
 * @deprecated 该方法废弃
 * 对评论的回复 事件的处理
 * @param comment_id  回复的评论的id
 */
function reply(comment_id){
    var activity_id = $(this).parent().parent('.comment-list')
        .attr("activity_id");
    var content_txt = $("#input"+activity_id).val();
    var reply_count = $("#"+comment_id).children(":last").children(":first")
        .children("a").children("b");
    if(content_txt!=""){
        $.ajax({
            type: "POST",
            url: "reply/"+current_login_user.clientId+"/add/"+ comment_id ,
            dataType: "json",
            contentType: "application/json",
            data:JSON.stringify({content:content_txt}),
            success: function (data) {
                if(data.code==404){
                    alert(data.message);
                }else {
//                    makeComment(data.comment,data.sys_time,activity_id);
                    makeReply(data.reply,data.sys_time,comment_id);
                }
                reply_count.text(parseInt(reply_count.text())+1);
            }
        });
    }else{
        alert("请输入回复信息");
    }
    $(this).prev().val("");
    return this;
}

/**
 * 未来的评论按钮会变成回复按钮，因此这里做个适配转换，根据方法改变按钮行为
 * @param func
 */
function setCommentButton(func,hook){
    if(hook==undefined||hook==""){
        $(".cmt_sub_btn").unbind("click");
        $(".cmt_sub_btn").on("click",func);
    }else{
        $("#input"+hook).parent().children(":last,.cmt_sub_btn").unbind("click");
        $("#input"+hook).parent().children(":last,.cmt_sub_btn").on("click",func);
    }

}
function setAgreementButton(){
    $(".agree-btn").on("click",function(){
        var client_id = current_login_user.clientId;
        var activity_id = $(this).parent(".trend-message-foot").parent(".trends-message-box")
                .parent("dd").parent("dl").attr("id");
        var agree_btn = $(this).children();
        var agree_count = $(this).children().children(":last");
        if(agree_btn.hasClass("clicked")){
            $.ajax({
                type: "POST",
                url: "activity/"+client_id+"/disagree/" + activity_id,
                dataType: "json",
                contentType: "application/json",
                success: function (data) {
                    if(data.code==200){
                        agree_count.text(parseInt(agree_count.text())-1);
                        agree_btn.removeClass("clicked");
                    }else{
                        alert(data.message);
                    }
                }
            });
        }else{
            $.ajax({
                type: "POST",
                url: "activity/"+client_id+"/agree/" + activity_id,
                dataType: "json",
                contentType: "application/json",
                success: function (data) {
                    if(data.code==200){
                        agree_count.text(parseInt(agree_count.text())+1);
                        agree_btn.addClass("clicked");
                    }else{
                        alert(data.message);
                    }
                }
            });
        }
    });
}
function setShareButton(){
    $(".share-btn").on("click",function(){
        var client_id = current_login_user.clientId;
        var activity_id = $(this).parent(".trend-message-foot").parent(".trends-message-box")
                .parent("dd").parent("dl").attr("id");
        var rooter_id = $(this).parent(".trend-message-foot").parent(".trends-message-box")
                .parent("dd").parent("dl").attr("rooter-id");
        var source_id = $(this).parent(".trend-message-foot").parent(".trends-message-box")
                .parent("dd").parent("dl").attr("source-id");
        var username = $(this).parent().parent().children(".trend-message-head").children("a").text();
        var content = $(this).parent().parent().children(".trend-message-body").children(":first").text();
        if(source_id=="deleted"||rooter_id=="deleted"){
            alertModal("这条动态作者都删除了还转发毛线～～");
            showModal();
        }else if(rooter_id==client_id){
            alertModal("自己的动态还是别转了吧！");
            showModal();
        }else if(source_id!=activity_id){
            content = $(this).parent().parent().children(".trend-message-body").children(".obscure-lab")
                    .next("p").text();
            username = $(this).parent().parent().children(".trend-message-body").children(".obscure-lab")
                    .children("a").children("b").text();
            shareBox(source_id,client_id,username,content);
            showModal();
        }else{
            shareBox(activity_id,client_id,username,content);
            showModal();
        }
    });
}
function setDeleteButton(){
    $(".delete-btn").on("click",function(){
        var client_id = current_login_user.clientId;
        var activity_id = $(this).parent(".trend-message-foot").parent(".trends-message-box")
                .parent("dd").parent("dl").attr("id");
        $.ajax({
            type: "DELETE",
            url: "activity/"+client_id+"/delete/" + activity_id,
            dataType: "json",
            contentType: "application/json",
            success: function (data) {
                //如果照片选择器还打开着，就关掉并清空。
                if(data.code==200){
                    initPicture();
                    $("#ModalLabel").html("<h3 >^_^ 您已成功删除一条动态</h3>");
                    $("#"+activity_id).slideUp(500, function () {
                        $(this).css("display", "none");
                    });
                    $("#"+activity_id).remove();
                    showModal();
                }else{
                    alert(data.message);
                }
            }
        });
    });
}

/**
 * 点击回复按钮后，转移到评论框，并使评论按钮变成回复按钮
 * @param hook  转向的锚点（input的id）
 * @param comment_id  回复的评论的id
 */
function replyBtnEffect(hook,comment_id,reply_to_name,reply_to_id){
    $('body,html').animate({scrollTop:$("#input"+hook).offset().top-250},500);
    $("#input"+hook).focus();
    //获取输入框后面的提交按钮
    if(!$("#input"+hook).parent().children(":last").hasClass("reply-btn")){
        $("#input"+hook).after("<a href='javascript:cancelReply(\""+hook+"\")'>取消回复</a>");
    }
    $("#input"+hook).parent().children(":last").removeClass("btn-yellow sign-in-btn").addClass("nav-blue")
        .addClass("reply-btn").children(".btn-txt").html("回复");
    $("#input"+hook).attr("placeholder","@"+reply_to_name);
    setCommentButton(function(){
        var content_txt = $("#input"+hook).val();
        //定位回复数量的数字
        var reply_count = $("#"+comment_id).children("dd").children("span").children(":first").children()
        if(content_txt!=""){
            $.ajax({
                type: "POST",
                url: "reply/"+current_login_user.clientId+"/add/"+ comment_id ,
                dataType: "json",
                contentType: "application/json",
                data:JSON.stringify({content:content_txt,toClient_id:reply_to_id}),
                success: function (data) {
                    if(data.code==404){
                        alert(data.message);
                    }else {
//                    makeComment(data.comment,data.sys_time,activity_id);
                        makeReply(data.reply,data.sys_time,comment_id);
                        var replies = $("#"+comment_id).children(":last");
                        replies.removeClass("hidden-block");
                        replies.addClass("show-block");
                        if(!reply_count.parent().next().hasClass("slide-btn")){
                            reply_count.parent().parent().append("<a href='javascript:void(0)' class='obscure-lab blod-big slide-btn left-10'>收起</a>");
                            //生成控件后，为其绑定事件
                            replyBtnBind();
                        }
                    }
                    reply_count.text(parseInt(reply_count.text())+1);
                }
            });
        }else{
            alert("请输入回复信息");
        }
        $("#input"+hook).val("");
    },hook);
}
/**
 * 展开查看详细回复信息
 */
function replyBtnBind(){
    $(".slide-btn").unbind("click");
    $(".slide-btn").on("click",function () {
        var replies = $(this).parent().parent("dd").next(".reply-list");
        var comment_id = $(this).parent().parent("dd").parent().attr("id");
        if (replies.hasClass("hidden-block")) {
            $.ajax({
                type: "GET",
                url: "reply/fetch?comment_id=" + comment_id,
                dataType: "json",
                contentType: "application/json",
                success: function (data) {
                    if (data.replies.length > 0) {
                        for(var i=0;i<data.replies.length;i++){
                            makeReply(data.replies[i],data.sys_time,comment_id);
                        }
                    }
                }
            });
            replies.slideDown(150,function () {
                replies.removeClass("hidden-block")
                replies.addClass("show-block");
            });
            $(this).html("收起");
        } else {
            replies.slideUp(200, function () {
                replies.removeClass("show-block")
                replies.addClass("hidden-block");
                $("#"+comment_id).children(".reply-list").children().remove();
            });
            $(this).html("下拉");
        }
    });
}
/**
 * 取消回复状态
 * @param input_hook    输入框锚点
 */
function cancelReply(input_hook){
    $("#input"+input_hook).parent().children(":last").removeClass("nav-blue reply-btn")
        .addClass("btn-yellow sign-in-btn").children(".btn-txt").html("评论");
    $("#input"+input_hook).next().remove();
    $("#input"+input_hook).attr("placeholder","忍不了了，我要说几句！");
    setCommentButton(comment,input_hook);
}

//判断目标用户是否在集合中（判断是否赞过，是否转发过用）
function containsUser(users,objId){
    for(var i=0;i<users.length;i++){
        if(users[i].clientId==objId){
            return true;
        }
    }
    return false;
}

function shareBox(activity_id,client_id,username,content){
    clearModal();
    var box_head = $("<div class='text-left paddingbottom-10'></div>");
    var content_input = $("<input type='text' id='content_title' class='length-8  form-control' placeholder='独逗逼不如众逗逼'>");
    box_head.append(content_input);
    var box_body = $("<div class='inner-box'></div>");
    var username_lab = $("<a href='#' class='username-lab15'># "+username+"</a>");
    var content_box = $("<p>"+content+"</p>");
    box_body.append(username_lab).append(content_box);
    var box_foot = $("<div class='text-right paddingtop-10'></div>");
    var shareBtn = $("<button class='btn sign-in-btn left-10 btn-yellow ' id='shareBtn'></button>");
    shareBtn.append("<i class='glyphicon glyphicon-pencil btn-icon'><span class='btn-txt'>转发</span></i>");
    box_foot.append(shareBtn);
    $("#ModalLabel").append(box_head).append(box_body).append(box_foot);
    share(activity_id,client_id,content);
}

function share(activity_id,client_id,content){
    $("#shareBtn").on("click", function () {
        $.ajax({
            type: "POST",
            url: "activity/"+client_id+"/share/" + activity_id,
            dataType: "json",
            contentType: "application/json",
            data:JSON.stringify({content:$("#content_title").val()}),
            success: function (data) {
                $("#trend-message-body").val("");
                initPicture();
                if(data.code==404){
                    alert(data.message);
                }else if(data.code==403){
                    alertModal(data.message);
                }else{
                    prependActivity(data.activity,data.sys_time);
                    $("#ModalLabel").html("<h3 >^_^ 您已成功转发一条动态</h3>");
                    flushPanelButton();
                }
            }
        });
    });
}

function clearModal(){
    $("#ModalLabel").html("");
}
function alertModal(message){
    clearModal();
    var content = $("<img src='assets/images/static/invalid/notfound.png' class='tiny-box' />");
    var alert = $("<h3 >"+message+"</h3>");
    $("#ModalLabel").append(content).append(alert);
}
/**
 * 刷新面板上的按钮
 */
function flushPanelButton(){
    //评论按钮
    flushCommentBtn();
    //点赞按钮
    setAgreementButton();
    //删除按钮
    setDeleteButton();
    //分享按钮
    setShareButton();
}

function makeTemplate(){
    var template = {
        dropdownlist:"<div class='dropdown-div'>" +
            "<ul class='dropdown-list'>" +
                "<li></li>" +
            "</ul>" +
        "</div>"
    }
}