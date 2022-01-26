package com.nxg.mvvm.ui

import com.nxg.mvvm.applicationViewModels
import com.nxg.mvvm.viewmodel.BaseSharedAndroidViewModel

open class BaseViewModelFragment : BaseFragment() {

    private val mBaseSharedAndroidViewModel: BaseSharedAndroidViewModel by applicationViewModels()

}