package xyz.nfcv.telephone_directory

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import net.sourceforge.pinyin4j.PinyinHelper
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType
import org.junit.Test
import org.junit.runner.RunWith
import xyz.nfcv.telephone_directory.model.Person

@RunWith(AndroidJUnit4::class)
class DatabasePersonTest {
    @Test
    fun getAll() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val persons = Person.allWithStatus(appContext)
        persons.forEach(::println)
    }

    @Test
    fun insert() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        Person.insert(appContext, Person(null, "xxx", "17xxxxxxxxxx", "xxx@zjut.edu.cn", "浙江省杭州市西湖区留下街道留和路288号浙江工业大学屏峰校区", "浙江省XX市XX县XX镇XX村", 0))
    }

    @Test
    fun like() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val personsLikeName = Person.likeName(appContext, "xx")
        val personsLikeAddress = Person.likeAddress(appContext, "XX村")
        personsLikeName.forEach(::println)
        personsLikeAddress.forEach(::println)
    }

    @Test
    fun update() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val person = Person.all(appContext)[0]
        person.email = "XXXXXX@qq.com"
        person.telephone = "123456778901"
        person.like = 1
        val result = Person.update(appContext, person)
    }

    @Test
    fun random() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val names = """
            段康德
            吕正志
            孟弘业
            沈博远
            顾正谊
            高信鸿
            罗建柏
            田安顺
            曾星火
            贾永长
            蒋弘懿
            熊高轩
            袁元白
            武凯康
            杜俊喆
            蔡俊楚
            高天罡
            范烨然
            魏永怡
            郝文林
            于天罡
            萧俊楚
            康高超
            钟和颂
            胡高韵
            白嘉实
            黎承志
            姚英卓
            金彭泽
            吴睿博
            尹翰飞
            姜恺歌
            秦高达
            龙远航
            宋同方
            谭嘉容
            罗承允
            姚元恺
            孙锐达
            蔡玉石
            黎元凯
            谢圣杰
            萧鸿轩
            邵明旭
            黄浩思
            龙星驰
            周乐章
            余茂典
            于奇胜
            卢英华
            吴凯定
            冯意智
            乔永长
            蒋浩初
            常晗日
            刘泰初
            蔡凯捷
            贺玉书
            董阳成
            朱开霁
            任文林
            陆琪睿
            蔡德泽
            侯弘壮
            毛致远
            周俊楚
            张德庸
            萧嘉熙
            董光启
            汪智敏
            彭哲茂
            唐星阑
            朱高扬
            陆正青
            蒋黎昕
            漕力勤
            高学海
            顾俊誉
            姜俊豪
            熊嘉运
            于凯泽
            蒋星津
            钟鹏云
            万才俊
            康高格
            吕嘉言
            杜国兴
            唐康胜
            贺凯风
            汤烨伟
            戴建明
            韩嘉庆
            易乐童
            龚乐安
            邵建本
            唐建明
            赖志行
            邹成仁
            廖景天
            金睿明
        """.trimIndent().lines()
        for (name in names) {
            val phone = (10000000000..19999999999).random().toString()
            val pinyin = PinyinHelper.toHanYuPinyinString(
                name.trim().uppercase(),
                HanyuPinyinOutputFormat().apply {
                    caseType = HanyuPinyinCaseType.LOWERCASE
                    toneType = HanyuPinyinToneType.WITHOUT_TONE
                    vCharType = HanyuPinyinVCharType.WITH_V
                },
                "",
                true
            )
            val email = "$pinyin@nfcv.xyz"
            val address = "浙江工业大学屏峰校区"
            Person.insert(appContext, Person(null, name, phone, email, address, address, 0, 1 shl 1, System.currentTimeMillis()))
        }
    }
}