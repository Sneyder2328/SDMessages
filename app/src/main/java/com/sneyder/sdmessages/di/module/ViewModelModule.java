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

package com.sneyder.sdmessages.di.module;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import com.sneyder.sdmessages.ViewModelProviderFactory;
import com.sneyder.sdmessages.di.ViewModelKey;
import com.sneyder.sdmessages.ui.home.HomeViewModel;
import com.sneyder.sdmessages.ui.login.LogInViewModel;
import com.sneyder.sdmessages.ui.main.MainViewModel;
import com.sneyder.sdmessages.ui.main.chats.ChatsViewModel;
import com.sneyder.sdmessages.ui.register.RegisterViewModel;
import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class ViewModelModule {

  @Binds
  @IntoMap
  @ViewModelKey(MainViewModel.class)
  abstract ViewModel bindMainViewModel(MainViewModel mainViewModel);

  @Binds
  @IntoMap
  @ViewModelKey(ChatsViewModel.class)
  abstract ViewModel bindChatsViewModel(ChatsViewModel chatsViewModel);

  @Binds
  @IntoMap
  @ViewModelKey(HomeViewModel.class)
  abstract ViewModel bindHomeViewModel(HomeViewModel homeViewModel);

  @Binds
  @IntoMap
  @ViewModelKey(RegisterViewModel.class)
  abstract ViewModel bindRegisterViewModel(RegisterViewModel registerViewModel);

  @Binds
  @IntoMap
  @ViewModelKey(LogInViewModel.class)
  abstract ViewModel bindLogInViewModel(LogInViewModel logInViewModel);

  @Binds
  abstract ViewModelProvider.Factory bindViewModelFactory(ViewModelProviderFactory factory);
}
