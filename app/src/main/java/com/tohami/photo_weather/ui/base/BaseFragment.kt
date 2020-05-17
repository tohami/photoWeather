package com.tohami.photo_weather.ui.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.tohami.photo_weather.data.model.StringModel
import com.tohami.photo_weather.utils.UIUtils.showToast
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import java.util.*

abstract class BaseFragment : Fragment() {
    protected lateinit var mContext: Context
    private var mListener: OnFragmentInteractionListener? = null

    private val mCompositeDisposables = CompositeDisposable()
    val navigationController: NavController?
        get() = view?.findNavController()


    @get:LayoutRes
    protected abstract val layoutID: Int
    protected abstract val toolbarTitle: String
    protected abstract val toolbarVisibility: Boolean

    protected abstract fun initViews(savedInstanceState: Bundle?)

    protected abstract fun setListeners()

    protected abstract fun bindViewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(layoutID, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (activity != null)
            mContext = this.activity as Context
        initViews(savedInstanceState)
        bindViewModels()
        setListeners()
    }

    protected fun onError(exception: Throwable) {
        exception.printStackTrace()
    }

    protected fun showMessage(message: Any) {
        when (message) {
            is String -> showToast(mContext, message)
            is Int -> showToast(mContext, getString(message))
            is StringModel -> showToast(mContext, message.getString(mContext, Locale.getDefault()))
        }
    }

    // Disposables
    fun addDisposable(disposable: Disposable): Boolean {
        return mCompositeDisposables.add(disposable)
    }

    private fun clearDisposables() {
        mCompositeDisposables.clear()
    }

    override fun onDestroyView() {
        clearDisposables()
        super.onDestroyView()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mListener = if (context is OnFragmentInteractionListener) {
            context
        } else {
            throw RuntimeException(
                context.toString()
                        + " must implement OnFragmentInteractionListener"
            )
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    override fun onStart() {
        super.onStart()
        mListener?.setToolbarVisibility(toolbarVisibility)
        mListener?.setToolbarTitle(toolbarTitle)
    }

}
