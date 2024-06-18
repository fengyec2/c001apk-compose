package com.example.c001apk.compose.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.isVisible
import com.example.c001apk.compose.logic.state.LoadingState
import com.example.c001apk.compose.ui.base.BaseViewModel
import com.example.c001apk.compose.util.ReportType

/**
 * Created by bggRGjQaUbCoE on 2024/6/10
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommonScreen(
    viewModel: BaseViewModel,
    refreshState: Boolean?,
    resetRefreshState: () -> Unit,
    paddingValues: PaddingValues,
    needTopPadding: Boolean = false,
    onViewUser: (String) -> Unit,
    onViewFeed: (String, Boolean) -> Unit,
    onOpenLink: (String, String?) -> Unit,
    onCopyText: (String?) -> Unit,
    isHomeFeed: Boolean = false,
    onReport: (String, ReportType) -> Unit,
    onViewFFFList: (String?, String, String?, String?) -> Unit,
    onHandleRecent: ((String, String, String, Int) -> Unit)? = null,
    onHandleMessage: ((String, Int) -> Unit)? = null,
    onViewChat: ((String, String, String) -> Unit)? = null,
) {

    val view = LocalView.current
    val layoutDirection = LocalLayoutDirection.current
    val state = rememberPullToRefreshState()
    val lazyListState = rememberLazyListState()

    LaunchedEffect(refreshState) {
        if (refreshState == true) {
            resetRefreshState()
            if (view.isVisible) {
                viewModel.refresh()
                lazyListState.scrollToItem(0)
            }
        }
    }

    val dataList = remember(key1 = viewModel.loadingState) {
        (viewModel.loadingState as? LoadingState.Success)?.response ?: emptyList()
    }

    PullToRefreshBox(
        modifier = Modifier.padding(
            start = paddingValues.calculateLeftPadding(layoutDirection),
            end = paddingValues.calculateRightPadding(layoutDirection),
            top = if (needTopPadding) paddingValues.calculateTopPadding() else 0.dp
        ),
        state = state,
        isRefreshing = viewModel.isRefreshing,
        onRefresh = viewModel::refresh,
        indicator = {
            PullToRefreshDefaults.Indicator(
                modifier = Modifier.align(Alignment.TopCenter),
                isRefreshing = viewModel.isRefreshing,
                state = state,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            contentPadding = PaddingValues(
                top = 10.dp,
                bottom = 10.dp + paddingValues.calculateBottomPadding()
            ),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            state = lazyListState
        ) {

            ItemCard(
                loadingState = viewModel.loadingState,
                dataList = dataList,
                loadMore = viewModel::loadMore,
                isEnd = viewModel.isEnd,
                onViewUser = onViewUser,
                onViewFeed = onViewFeed,
                onOpenLink = onOpenLink,
                onCopyText = onCopyText,
                onShowTotalReply = { _, _, _ -> },
                isHomeFeed = isHomeFeed,
                onReport = onReport,
                onViewFFFList = onViewFFFList,
                onLike = { id, like, likeType ->
                    viewModel.onLike(id, like, likeType)
                },
                onDelete = { id, deleteType ->
                    viewModel.onDelete(id, deleteType)
                },
                onBlockUser = { uid ->
                    viewModel.onBlockUser(uid)
                },
                onFollowUser = { uid, isFollow ->
                    viewModel.onFollowUser(uid, isFollow)
                },
                onHandleRecent = onHandleRecent,
                onHandleMessage = onHandleMessage,
                onViewChat = onViewChat,
            )

            FooterCard(
                modifier = Modifier.padding(horizontal = 10.dp),
                footerState = viewModel.footerState,
                loadMore = viewModel::loadMore,
                isFeed = false
            )

        }
    }

}