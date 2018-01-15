/*
 * Copyright (C) 2018 Sneyder Angulo.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sneyder.sdmessages.ui.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import com.sneyder.sdmessages.R
import com.sneyder.sdmessages.ui.base.DaggerActivity
import com.sneyder.sdmessages.utils.RxSearchObservable
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_search.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SearchActivity : DaggerActivity(), HasSupportFragmentInjector {

    companion object {

        fun starterIntent(context: Context): Intent {
            return Intent(context, SearchActivity::class.java)
        }

    }

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    override fun supportFragmentInjector(): AndroidInjector<Fragment> = dispatchingAndroidInjector

    private val searchPagerAdapter by lazy { SearchPagerAdapter(this@SearchActivity, supportFragmentManager) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        initAWSMobileClient()

        tabLayout.tabMode = TabLayout.MODE_FIXED
        viewPager.adapter = searchPagerAdapter
        tabLayout.setupWithViewPager(viewPager)

        RxSearchObservable.fromEditText(searchEditText)
                .debounce(500, TimeUnit.MILLISECONDS)
                .distinctUntilChanged()
                .map { it.trim() }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    searchPagerAdapter.registeredSearchListeners[viewPager.currentItem]?.search(it)
                }
    }

}
