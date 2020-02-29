package com.angcyo.uicore.dslitem

import com.angcyo.uicore.demo.R
import kotlin.random.Random.Default.nextInt

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/01/20
 */

/**随机获取一张图片地址*/

val imageUrlList = listOf(
    "https://p1.hiclipart.com/preview/903/399/606/166-olivia-holt-woman-wearing-sunglasses-png-clipart.jpg",
    "http://img.lanrentuku.com/img/allimg/1908/156687113078.jpg",
    "https://cdn.imgbin.com/2/11/4/imgbin-model-sunglasses-christian-dior-se-sunglasses-model-woman-taking-selfie-LPcsScGiz1VkS9amyh3fiAxx3.jpg",
    "http://img.lanrentuku.com/img/allimg/1908/15668899471337.jpg",
    "https://image.shutterstock.com/image-photo/bright-spring-view-cameo-island-260nw-1048185397.jpg",
    "https://keenthemes.com/preview/metronic/theme_rtl/assets/global/plugins/jcrop/demos/demo_files/image1.jpg",
    "http://b-ssl.duitang.com/uploads/item/201509/18/20150918071714_m3ZPi.thumb.700_0.jpeg",
    "http://b-ssl.duitang.com/uploads/item/201712/09/20171209095824_2J4Se.jpeg",
    "https://res.cloudinary.com/demo/image/upload/c_imagga_crop/family_bench.jpg",
    "https://getbootstrapadmin.com/remark/global/photos/people-7-960x640.jpg",
    "https://www.dreamgrow.com/wp-content/uploads/2016/09/Facebook-cheat-sheet-cover-720x512.jpg",
    "http://i0.hdslb.com/bfs/article/e4d73e93d8d83714316bdffcbe2dc46e9534a855.jpg",
    "http://c.hiphotos.baidu.com/zhidao/pic/item/83025aafa40f4bfb51b5edac014f78f0f63618e4.jpg",
    "http://pic1.win4000.com/wallpaper/1/57b2d0a2dce6a.jpg",
    "http://b-ssl.duitang.com/uploads/item/201806/01/20180601001417_vLxxA.thumb.700_0.jpeg",
    "http://uploads.5068.com/allimg/1712/151-1G20F95109.jpg",
    "http://2e.zol-img.com.cn/product/51_500x2000/302/ceRQU6DQBV3aU.jpg",
    "http://b-ssl.duitang.com/uploads/item/201511/19/20151119143731_MXfmB.png",
    "http://www.cpnic.com/UploadFiles/img_1_4205803539_2109898680_26.jpg",
    "http://b-ssl.duitang.com/uploads/item/201511/19/20151119143731_MXfmB.png",
    "http://pic1.win4000.com/mobile/2018-10-19/5bc96fed96df0.jpg",
    "http://i0.hdslb.com/bfs/face/9969e592e914d6dba2c65d33a349212d77b197a8.jpg",
    "http://pic1.win4000.com/mobile/1/521ec5e08264e.jpg",
    "http://img.mm8mm8.com/40/77/40775e9d7782b43fe0004569db9c1ede.jpg",
    "http://b-ssl.duitang.com/uploads/item/201807/13/20180713184814_szymk.thumb.700_0.jpeg",
    "http://pic1.win4000.com/mobile/2017-11-20/5a129df9b0e9e.jpg",

    //gif
    "http://5b0988e595225.cdn.sohucs.com/images/20180921/a5d4da04b8724025a56746106b92f338.gif",
    "http://5b0988e595225.cdn.sohucs.com/images/20180516/5e7c11d54dfa49399e12500835d1dcd5.gif",
    "http://5b0988e595225.cdn.sohucs.com/images/20171116/1cd5d3f5881a48979e4528a3c9da3dd6.gif",
    "http://5b0988e595225.cdn.sohucs.com/images/20180615/ffa1e2a4bb814099892bc6c380d03cd6.gif",
    "http://2a.zol-img.com.cn/product/94_800x600/130/ceRBIlALIZGnI.gif",
    "http://i-1.52miji.com/2018/8/15/b5ad85d0-b9cf-43e4-bd94-5e314d054f43.gif",
    "http://img.9553.com/uploadfile/2016/1231/20161231030107608.gif",
    "http://5b0988e595225.cdn.sohucs.com/images/20171130/caab9b84d7ff459e89fcc03cee11d9bb.gif",
    "http://5b0988e595225.cdn.sohucs.com/images/20171227/157724ff25b9415e8853050a58e4a581.gif"
)

val imageResList = listOf(
    R.drawable.angry,
    R.drawable.calm,
    R.drawable.happy,
    R.drawable.heart,
    R.drawable.light,
    R.drawable.melancholy,
    R.drawable.tire,
    R.drawable.wink,
    R.drawable.worry
)

fun image(): String {
    return imageUrlList[nextInt(imageUrlList.size)]
}

fun res(): Int {
    return imageResList[nextInt(imageResList.size)]
}