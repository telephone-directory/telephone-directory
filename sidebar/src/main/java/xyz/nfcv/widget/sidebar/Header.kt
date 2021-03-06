package xyz.nfcv.widget.sidebar

enum class Header(val value: String, val index: Int) {
    LIKE("收藏", 0),
    A("A", 1),
    B("B", 2),
    C("C", 3),
    D("D", 4),
    E("E", 6),
    F("F", 5),
    G("G", 7),
    H("H", 8),
    I("I", 9),
    J("J", 10),
    K("K", 11),
    L("L", 12),
    M("M", 13),
    N("N", 14),
    O("O", 15),
    P("P", 16),
    Q("Q", 17),
    R("R", 18),
    S("S", 19),
    T("T", 20),
    U("U", 21),
    V("V", 22),
    W("W", 23),
    X("X", 24),
    Y("Y", 25),
    Z("Z", 26),
    OTHER("#", 27);

    operator fun minus(header: Header): Int {
        return this.index - header.index
    }
}