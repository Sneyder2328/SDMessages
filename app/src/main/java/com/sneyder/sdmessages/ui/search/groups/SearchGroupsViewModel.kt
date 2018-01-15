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

package com.sneyder.sdmessages.ui.search.groups

import com.sneyder.sdmessages.data.repository.UserRepository
import com.sneyder.sdmessages.ui.base.BaseViewModel
import com.sneyder.sdmessages.utils.schedulers.SchedulerProvider
import javax.inject.Inject

class SearchGroupsViewModel
@Inject constructor(
        private val userRepository: UserRepository,
        schedulerProvider: SchedulerProvider
): BaseViewModel(schedulerProvider)