package com.angcyo.uicore

import android.app.Application
import com.angcyo.canvas.render.util.toDrawable
import com.angcyo.canvas2.laser.pecker.dslitem.control.TypefaceItem
import com.angcyo.canvas2.laser.pecker.manager.LPProjectManager
import com.angcyo.canvas2.laser.pecker.manager.ShareProjectInfo
import com.angcyo.canvas2.laser.pecker.toRendererList
import com.angcyo.core.CoreApplication
import com.angcyo.laserpacker.LPDataConstant
import com.angcyo.laserpacker.open.CanvasOpenModel
import com.angcyo.laserpacker.open.CanvasOpenPreviewActivity
import com.angcyo.laserpacker.project.dslitem.ProjectListItem
import com.angcyo.library.annotation.CallPoint
import com.angcyo.library.component.FontManager
import com.angcyo.library.ex.file
import com.angcyo.library.ex.isDebug
import com.angcyo.library.ex.isDebugType
import com.angcyo.library.ex.save
import com.angcyo.library.ex.toBitmapOfBase64
import com.angcyo.library.libCacheFile
import com.angcyo.library.utils.uuid
import com.angcyo.objectbox.laser.pecker.LPBox
import com.angcyo.objectbox.laser.pecker.entity.EntitySync
import com.angcyo.objectbox.laser.pecker.entity.MaterialEntity
import com.angcyo.tbs.openUrlWithTbs
import com.angcyo.uicore.demo.Canvas2Demo
import com.angcyo.uicore.demo.R

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2023/07/26
 */
object LPAppHelper {

    /**初始化LP App对应的功能
     * ```
     * dev: com.hingin.rn.hiprint
     * lds: com.hingin.lp1.hiprint
     * ```
     * */
    @CallPoint
    fun initLPApp(application: Application) {
        //LaserPecker
        LPBox.init(application)

        CanvasOpenModel.OPEN_ACTIVITY_CLASS = MainActivity::class.java
        //CanvasOpenModel.OPEN_ACTIVITY_FRAGMENT_CLASS = CanvasDemo::class.java
        CanvasOpenModel.OPEN_ACTIVITY_FRAGMENT_CLASS = Canvas2Demo::class.java

        TypefaceItem.getTypefaceItemSyncStateRes = {
            if (isDebugType()) {
                if (it.itemTypefaceInfo?.isCustom == true) {
                    R.drawable.canvas_synchronized_svg
                } else {
                    null
                }
            } else {
                null
            }
        }

        ProjectListItem.getProjectListSyncStateRes = {
            if (isDebugType()) {
                R.drawable.canvas_synchronized_svg
            } else {
                null
            }
        }

        MaterialEntity.getMaterialItemSyncStateRes = {
            if (isDebugType()) {
                R.drawable.canvas_synchronized_svg
            } else {
                null
            }
        }

        CanvasOpenPreviewActivity.convertElementBeanListToDrawable = {
            it?.toRendererList()?.toDrawable()
        }

        CoreApplication.onOpenUrlAction = { url ->
            openUrlWithTbs(url)
        }

        //字体同步
        FontManager.importCustomFontActionList.add {
            EntitySync.saveFontSyncEntity(it)
        }
        FontManager.deleteCustomFontActionList.add {
            EntitySync.deleteFontSyncEntity(it)
        }

        //分享工程
        ProjectListItem.onShareProjectAction = { bean, projectName ->
            val file = bean._filePath?.file()
            if (isDebug()) {
                ProjectListItem.projectFileShareAction(bean, projectName)
            } else if (file != null) {
                val bitmap = bean._previewImgBitmap ?: bean.preview_img?.toBitmapOfBase64()
                val previewFile = libCacheFile("${uuid()}${LPDataConstant.EXT_PREVIEW}")
                bitmap?.save(previewFile)
                LPProjectManager.onShareProjectAction.invoke(
                    ShareProjectInfo(null, file, previewFile, bean, projectName)
                )
            }
        }
    }
}