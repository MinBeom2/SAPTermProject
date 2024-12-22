package kr.ac.kumoh.ce.s20200166.termproject

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn

import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil3.compose.rememberAsyncImagePainter
import kotlinx.coroutines.launch
import kr.ac.kumoh.ce.s20200166.termproject.ui.theme.TermProjectTheme

class MainActivity : ComponentActivity() {
    private val gameViewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TermProjectTheme {
                MainScreen(gameViewModel)
            }
        }
    }
}

@Composable
fun MainScreen(viewModel: GameViewModel) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val navController = rememberNavController()
    val gameList by viewModel.gameList.observeAsState(emptyList())
    val gameInfoList by viewModel.gameInfoList.observeAsState(emptyList())
    val gameImageList by viewModel.gameImageList.observeAsState(emptyList())

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerSheet(drawerState) {
                navController.navigate(it){
                    launchSingleTop = true
                    popUpTo(it) { inclusive = true}  //stack이 계속 쌓이는 것 방지
                }
            }
        },
        gesturesEnabled = true,
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopBar(drawerState, navController)
            },
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "gamelist", //초기 페이지 게임리스트 페이지로 설정
                modifier = Modifier.padding(innerPadding),
            ) {
                composable("gamelist") {
                    GameListScreen(gameList, gameInfoList, gameImageList)
                }
                composable("appintroduce") {
                    AppIntroduceScreen(navController)
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(drawerState: DrawerState, navController: NavController) {
    CenterAlignedTopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.steam), //drawable에 저장한 steam이미지 사용
                    contentDescription = "Steam icon",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            navController.navigate("gamelist") {
                                launchSingleTop = true
                                popUpTo("gamelist") { inclusive = true}
                            }// 해당 아이콘을 클릭 시 gamelist의 최상단으로 이동

                        }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Good Game Great Game", modifier = Modifier.clickable {
                    navController.navigate("gamelist") {
                        launchSingleTop = true
                        popUpTo("gamelist") { inclusive = true}
                    } //텍스트 클릭 시 gamelist의 최상단으로 이동
                })
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
    )
}


@Composable
fun DrawerSheet(
    drawerState: DrawerState,
    onNavigate: (String) -> Unit,
) {
    val scope = rememberCoroutineScope()

    ModalDrawerSheet {
        DrawerNavigationItem("게임 리스트", "gamelist", onNavigate, drawerState)
        DrawerNavigationItem("앱 소개", "appintroduce", onNavigate, drawerState)
    }
}

//게임 리스트 스크린
@Composable
fun GameListScreen(
    gameList: List<Game>,
    gameInfoList: List<GameInfo>,
    gameImageList: List<GameImage>
) {
    LazyColumn(
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(gameList) { game ->
            var expanded by remember { mutableStateOf(false) }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable { expanded = !expanded }, //카드 클릭시 상세 정보 출력
                elevation = CardDefaults.cardElevation(8.dp),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // gameImageList에서 현재 게임에 해당하는 이미지를 찾음
                        val gameImage = gameImageList.find { it.game_id == game.game_id }
                        if (gameImage != null) {
                            Image(
                                painter = rememberAsyncImagePainter(gameImage.image_url),
                                contentDescription = "게임 이미지 ${game.name}",
                                modifier = Modifier
                                    .clip(RoundedCornerShape(percent = 10))
                            )
                        }
                        Spacer(Modifier.width(16.dp))
                        Column(Modifier.weight(1f)) {
                            Text(game.name)
                        }
                    }

                    // CARD 클릭 시 상세 정보 출력
                    AnimatedVisibility(expanded) {
                        Column(Modifier.padding(top = 16.dp)) {
                            val gameInfo = gameInfoList.find { it.game_id == game.game_id } //상세 정보의 game_id와 game클래스의 game_id가 같은경우 상세정보 출력
                            if (gameInfo != null) {
                                Text("가격: ${gameInfo.price}")
                                Text("평점: ${gameInfo.rating}")
                                Text("출시 연도: ${gameInfo.release_year}")
                                Text("개발자: ${gameInfo.developer}")
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun DrawerNavigationItem(label: String, route: String, onNavigate: (String) -> Unit, drawerState: DrawerState) {
    val scope = rememberCoroutineScope()
    NavigationDrawerItem(
        label = { Text(label) },
        selected = false,
        onClick = {
            onNavigate(route)
            scope.launch { drawerState.close() }
        }
    )
}


//앱 소개 스크린
@Composable
fun AppIntroduceScreen(navController: NavController) {
    val appIntroduction = """
        이 앱은 게임 정보를 쉽게 탐색할 수 있도록 설계되었다. 
        여러 게임의 가격, 평점, 출시 연도, 개발자와 같은 세부 정보를 제공한다.
    """.trimIndent()

    val features = listOf(
        "게임 리스트 보기",
        "게임 상세 정보 확인",
        "게임 이미지 보기"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "앱 소개",
            style = MaterialTheme.typography.headlineSmall
        )

        Text(
            text = appIntroduction,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(8.dp)) // 공백 추가

        Text(
            text = "주요 기능",
            style = MaterialTheme.typography.headlineSmall
        )

        features.forEachIndexed { index, feature ->
            Text(
                text = "${index + 1}. $feature",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
