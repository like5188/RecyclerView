package com.like.recyclerview.sample.paging3.adapter

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import com.like.recyclerview.adapter.BasePagingDataAdapter
import com.like.recyclerview.sample.paging3.data.model.Article
import com.like.recyclerview.sample.paging3.data.model.TopArticle

class PagingDataAdapter : BasePagingDataAdapter<Any, ViewDataBinding>(
    object : DiffUtil.ItemCallback<Any>() {
        // 比对新旧条目是否是同一个条目
        override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
            return when {
                oldItem is Article && newItem is Article -> {
                    oldItem.id == newItem.id
                }
                oldItem is TopArticle && newItem is TopArticle -> {
                    oldItem.id == newItem.id
                }
                else -> false
            }
        }

        // 当上面的方法确定是同一个条目之后，这里比对条目的内容是否一样，不一样则会更新条目UI
        // 建议这里的比对把UI展示的数据都写上，写漏了会导致UI不更新对应字段；
        override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
            return when {
                oldItem is Article && newItem is Article -> {
                    oldItem.title == newItem.title
                }
                oldItem is TopArticle && newItem is TopArticle -> {
                    oldItem.title == newItem.title
                }
                else -> false
            }
        }

        // 告诉Adapter对这个Item进行局部的更新而不是整个更新。
        // 在areItemsTheSame()返回true，而areContentsTheSame()返回false时被回调的，也就是一个Item的内容发生了变化，而这个变化有可能是局部的（例如微博的点赞，我们只需要刷新图标而不是整个Item）。
        // 所以可以在getChangePayload()中封装一个Object来告诉RecyclerView进行局部的刷新。
        // 返回的这个对象会在什么地方收到呢？实际上在RecyclerView.Adapter中有两个onBindViewHolder方法，一个是我们必须要重写的，而另一个的第三个参数就是一个payload的列表(onBindViewHolder(RecyclerView.ViewHolder holder, int position, List payloads))。
        // 通常通过返回Bundle来传递数据
        override fun getChangePayload(oldItem: Any, newItem: Any): Any? {
            return super.getChangePayload(oldItem, newItem)
        }
    }
)