(function(){
    "use strict";

    function ownKeys(e,n){
        var t=Object.keys(e);
        if(Object.getOwnPropertySymbols){
            var o=Object.getOwnPropertySymbols(e);
            n&&(o=o.filter((function(n){
                return Object.getOwnPropertyDescriptor(e,n).enumerable
            }))),t.push.apply(t,o)
        }
        return t
    }

    function _objectSpread(e){
        for(var n=1;n<arguments.length;n++){
            var t=null!=arguments[n]?arguments[n]:{};
            n%2?ownKeys(Object(t),!0).forEach((function(n){
                _defineProperty(e,n,t[n])
            })):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(t)):ownKeys(Object(t)).forEach((function(n){
                Object.defineProperty(e,n,Object.getOwnPropertyDescriptor(t,n))
            }))
        }
        return e
    }

    function _defineProperty(e,n,t){
        return n in e?Object.defineProperty(e,n,{
            value:t,enumerable:!0,configurable:!0,writable:!0
        }):e[n]=t,e
    }
    var POS_PREFIX_142="--pos-banner-fluid-142__", posOptionsInitialBanner142={
        "container-padding":"0px",
        "container-direction":"column",
        "container-align-items":"auto",
        "decor-height":"273px",
        "decor-size":"auto",
        "decor-position":"50% 100%",
        "decor-display":"block",
        "decor-order":"1",
        decorFlex:"0 0 auto",
        "bg-url":"url('https://pos.gosuslugi.ru/bin/banner-fluid/142/banner-fluid-bg-142-1.svg')",
        "content-bg-url":"none",
        "content-margin":"0",
        "content-padding":"33px 25px 66px 25px",
        "content-order":"2",
        contentFlex:"0 0 auto",
        "text-margin":"0 0 24px 0",
        btnWidth:"100%",
        textFS:"20px",
        textLH:"28px",
        logoLeft:"0",
        logoRight:"auto",
        logoRadiusRight:"10px",
        logoRadiusLeft:"0"
    },setStyles=function(e,n){
        var t=arguments.length>2&&void 0!==arguments[2]?arguments[2]:POS_PREFIX_142;
        Object.keys(e).forEach((function(o){
            n.style.setProperty(t+o,e[o])
        }))
    },removeStyles=function(e,n){
        var t=arguments.length>2&&void 0!==arguments[2]?arguments[2]:POS_PREFIX_142;
        Object.keys(e).forEach((function(e){
            n.style.removeProperty(t+e)
        }))
    };

    function changePosBannerOnResize(){
        var e=document.documentElement,n=_objectSpread({},posOptionsInitialBanner142),
            t=document.getElementById("js-show-iframe-wrapper"),
            o=t?t.offsetWidth:document.body.offsetWidth;
        o>=405&&(n.btnWidth="auto",n["bg-url"]="url('https://pos.gosuslugi.ru/bin/banner-fluid/142/banner-fluid-bg-142-2.svg')",
            n["content-padding"]="20px 25px 30px 25px",
            n.textFS="24px",
            n.textLH="32px"),
        o>=585&&(n["container-direction"]="row",
            n["decor-height"]="auto",
            n.decorFlex="0 0 245px",
            n["decor-order"]="2",
            n.contentFlex="1 1 auto",
            n["content-order"]="1",
            n.logoRight="0",
            n.logoLeft="auto",
            n.logoRadiusRight="0",
            n.logoRadiusLeft="10px",
            n["bg-url"]="url('https://pos.gosuslugi.ru/bin/banner-fluid/142/banner-fluid-bg-142-1.svg')",
            n["content-padding"]="80px 25px 80px 25px",
            n["decor-position"]="0 50%"),
        o>=799&&(n["content-padding"]="64px 373px 64px 52px",
            n["decor-display"]="none",
            n["content-bg-url"]="url('https://pos.gosuslugi.ru/bin/banner-fluid/142/banner-fluid-bg-142-3.svg')",
            n["decor-position"]="calc(100% - 142px) 50%",
            n.textFS="32px",
            n.textLH="40px"),
        o>=1115&&(n["content-padding"]="70px 373px 70px 70px",
            n.textFS="36px",
            n.textLH="40px",
            n["content-bg-url"]="url('https://pos.gosuslugi.ru/bin/banner-fluid/142/banner-fluid-bg-142-4.svg')"),
            setStyles(n,e)}changePosBannerOnResize(),
        window.addEventListener("resize",changePosBannerOnResize),
        window.onunload=function(){
            var e=document.documentElement, n=_objectSpread({}, posOptionsInitialBanner142);
            window.removeEventListener("resize",changePosBannerOnResize), removeStyles(n,e)};
})()