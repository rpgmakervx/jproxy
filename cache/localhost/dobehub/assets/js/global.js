/**
 * Created by Administrator on 2015/11/13.
 */
$(function () {
    var PIC_SIZE = 153600;
    //默认首页为 校友动态
    //顶部导航栏的点击颜色变换策略
    $(".navleft > .items > a").on("click",function () {

    });
    //搜索框点击后出现下拉模板
    $(".input-txt").on("focus",function(){
        $("#syn-content").html("搜索内容："+$(".input-txt").val());
        $("#dropdown-mod").css("display","block");
    });
    //搜索框失去焦点后下拉模板消失
    $(".input-txt").on("blur",function(){
        setTimeout($('#dropdown-mod').fadeOut(),5000);
        $("#syn-content").html("搜索内容："+$(".input-txt").val());
    });
    //搜索框中输入内容，慕模板中内容同步
    $(".input-txt").keyup(function () {
        var txt = $(this).val()+"";
        $("#syn-content").html("搜索内容："+txt)
    });

    $("")
})

function flush(type){
    $("ul > li").removeClass("active");
    $("ul > li > a").css("font-weight","normal");
    $("#"+type).addClass("active");
    window.location.href="/"+type;
}