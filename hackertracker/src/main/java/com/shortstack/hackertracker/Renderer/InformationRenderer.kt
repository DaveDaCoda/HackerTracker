package com.shortstack.hackertracker.Renderer

import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pedrogomez.renderers.Renderer
import com.shortstack.hackertracker.BottomSheet.InformationBottomSheetDialogFragment
import com.shortstack.hackertracker.Model.Information
import com.shortstack.hackertracker.R
import kotlinx.android.synthetic.main.row_info.view.*

class InformationRenderer : Renderer<Information>(), View.OnClickListener {

    override fun inflate(inflater: LayoutInflater, parent: ViewGroup): View {
        return inflater.inflate(R.layout.row_info, parent, false)
    }

    override fun hookListeners(rootView: View?) {
        rootView!!.setOnClickListener(this)
    }

    override fun render(payloads: List<Any>) {
        rootView.header.text = content.title
    }


    override fun onClick(view: View) {
        val badges = InformationBottomSheetDialogFragment.newInstance(content)
        badges.show((context as AppCompatActivity).supportFragmentManager, badges.tag)
    }
}
