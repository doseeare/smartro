package ru.smartro.worknote.adapter

import android.content.Context
import android.util.DisplayMetrics
import android.view.ViewGroup
import android.view.WindowManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.item_image.view.*
import ru.smartro.worknote.R
import ru.smartro.worknote.adapter.listener.ImageClickListener
import ru.smartro.worknote.base.GenericRecyclerAdapter
import ru.smartro.worknote.base.ViewHolder
import ru.smartro.worknote.service.database.entity.work_order.ImageEntity
import ru.smartro.worknote.util.MyUtil


class GalleryPhotoAdapter(private val listener: ImageClickListener,
                          val context: Context, items: ArrayList<ImageEntity>) : GenericRecyclerAdapter<ImageEntity>(items) {
    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private val outMetrics = DisplayMetrics()

    override fun bind(item: ImageEntity, holder: ViewHolder) {
        @Suppress("DEPRECATION")
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            val display = context.display
            display?.getRealMetrics(outMetrics)
        } else {
            val display = windowManager.defaultDisplay
            display.getMetrics(outMetrics)
        }
        holder.itemView.item_imageview.layoutParams.height = outMetrics.widthPixels / 3
        holder.itemView.item_imageview.layoutParams.width = outMetrics.widthPixels / 3

        holder.itemView.item_remove.setOnClickListener {
            listener.imageRemoveClicked(item)
        }

        holder.itemView.item_imageview.setOnClickListener {
            listener.imageDetailClicked(item)
        }
        val myOptions = RequestOptions().override(100, 100)

        Glide.with(holder.itemView).asBitmap().apply(myOptions).load(MyUtil.base64ToImage(item.image))
            .into(holder.itemView.item_imageview)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return super.onCreateViewHolder(parent, R.layout.item_image)
    }

}