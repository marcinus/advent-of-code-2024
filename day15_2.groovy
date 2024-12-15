def sampleInput = '''##########
#..O..O.O#
#......O.#
#.OO..O.O#
#..O@..O.#
#O#..O...#
#O..O..O.#
#.OO.O.OO#
#....O...#
##########

<vv>^<v^>v>^vv^v>v<>v^v<v<^vv<<<^><<><>>v<vvv<>^v^>^<<<><<v<<<v^vv^v>^
vvv<<^>^v^^><<>>><>^<<><^vv^^<>vvv<>><^^v>^>vv<>v<<<<v<^v>^<^^>>>^<v<v
><>vv>v^v^<>><>>>><^^>vv>v<^^^>>v^v^<^^>v^^>v^<^v>v<>>v^v^<v>v^^<^^vv<
<<v<^>>^^^^>>>v^<>vvv^><v<<<>^^^vv^<vvv>^>v<^^^^v<>^>vvvv><>>v^<<^^^^^
^><^><>>><>^^<<^^v>>><^<v>^<vv>>v>>>^v><>^v><<<<v>>v<v<v>vvv>^<><<>^><
^>><>^v<><^vvv<^^<><v<<<<<><^v<<<><<<^^<v<^^^><^>>^<v^><<<^>>^v<v^v<v^
>^>>^v>vv>^<<^v<>><<><<v<<v><>v<^vv<<<>^^v^>^^>>><<^v>>v^v><^^>>^<>vv^
<><^^>^^^<><vvvvv^v<v<<>^v<v>v<<^><<><<><<<^^<<<^<<>><<><^^^>^^<>^>v<>
^^>vv<^v^v<vv>^<><v<^v>^^^>>>^^vvv^>vvv<>>>^<^>>>>>^<<^v>^vvv<>^<><<v>
v^^>>><<^^<>>^v^<v^vv<>v^<<>^<^v^v><^<<<><<^<v><v<>vv>>v><v^<vv<>v^<<^'''

static def getNext(def pos, def dir) {
    [pos[0] + dir[0], pos[1] + dir[1]]
}

static def parse(List<String> input) {
    int indexOfEmptyLine = input.findIndexOf { it.isEmpty() }
    def map = doubleMap(input.take(indexOfEmptyLine))
    int i = (0..<map.size()).find {
        map[it].contains('@')
    }
    def j = map[i].indexOf('@')
    def commands = input.drop(indexOfEmptyLine + 1).join('')
    [[i, j], map, commands]
}

static def doubleMap(List<String> map) {
    map.collect {
        StringBuilder sb = new StringBuilder()
        (0..<it.size()).each { i ->
            if (it[i] == '@') {
                sb.append('@.')
            } else if (it[i] == 'O') {
                sb.append('[]')
            } else {
                sb.append(it[i] * 2)
            }
        }
        sb
    }
}

enum DIR {
    UP('^', [-1, 0]), RIGHT('>', [0, 1], true), DOWN('v', [1, 0]), LEFT('<', [0, -1], true)

    def sign
    def dir
    boolean horizontal

    DIR(sign, dir, boolean horizontal = false) {
        this.sign = sign
        this.dir = dir
        this.horizontal = horizontal
    }
}

static def feasibleMoveHorizontal(def map, def pos, def dir) {
    def next = getNext(pos, dir)
    def nextElement = getAt(map, next)
    while (nextElement == '[' || nextElement == ']') {
        next = getNext(next, dir)
        nextElement = getAt(map, next)
    }
    return nextElement == '.'
}

static def doMoveHorizontal(def map, def pos, def dir) {
    def next = getNext(pos, dir)
    def nextElement = getAt(map, next)
    def prevElement = '@'
    setAtPos(map, pos, '.')
    while (nextElement == '[' || nextElement == ']') {
        setAtPos(map, next, prevElement)
        prevElement = nextElement
        next = getNext(next, dir)
        nextElement = getAt(map, next)
    }
    setAtPos(map, next, prevElement)
}

static def feasibleMoveVertical(def map, def pos, def dir) {
    def next = getNext(pos, dir)
    def nextItem = getAt(map, next)
    if (nextItem == '.') return true
    if (nextItem == '#') return false
    def layer = [next]
    if (nextItem == '[') {
        layer.add(getNext(next, DIR.RIGHT.dir))
    } else if (nextItem == ']') {
        layer.add(getNext(next, DIR.LEFT.dir))
    }
    while (!layer.isEmpty()) {
        def nextLayer = [] as Set
        for (int i = 0; i < layer.size(); i++) {
            next = getNext(layer[i], dir)
            nextItem = getAt(map, next)
            if (nextItem == '.') {
                continue
            } else if (nextItem == '#') {
                return false
            } else {
                nextLayer.add(next)
                if (nextItem == '[') {
                    nextLayer.add(getNext(next, DIR.RIGHT.dir))
                } else if (nextItem == ']') {
                    nextLayer.add(getNext(next, DIR.LEFT.dir))
                }
            }
        }
        layer = nextLayer
    }
    true
}

static def setAtPos(def map, def pos, String c) {
    map[pos[0]].replace(pos[1], pos[1]+1, c)
}

static def doMoveVertical(def map, def pos, def dir) {
    setAtPos(map, pos, '.')
    def layer = [[pos, '@']]
    while (!layer.isEmpty()) {
        def nextLayer = [] as Set
        for (int i = 0; i < layer.size(); i++) {
            def next = getNext(layer[i][0], dir)
            def nextItem = getAt(map, next)
            if (nextItem != '.') {
                setAtPos(map, next, '.')
                nextLayer.add([next, nextItem])
                if (nextItem == '[') {
                    nextLayer.add([getNext(next, DIR.RIGHT.dir), ']'])
                    setAtPos(map, getNext(next, DIR.RIGHT.dir), '.')
                } else if (nextItem == ']') {
                    nextLayer.add([getNext(next, DIR.LEFT.dir), '['])
                    setAtPos(map, getNext(next, DIR.LEFT.dir), '.')
                }
            }
        }
        layer.each {
            setAtPos(map, getNext(it[0], dir), it[1])
        }
        layer = nextLayer
    }
    getNext(pos, dir)
}

static def getAt(def map, def pos) {
    map[pos[0]][pos[1]]
}

// note: modifies map
static def move(def start, DIR dir, def map) {
    if (dir.horizontal) {
        if(feasibleMoveHorizontal(map, start, dir.dir)) {
            doMoveHorizontal(map, start, dir.dir)
            return getNext(start, dir.dir)
        }
    } else {
        if(feasibleMoveVertical(map, start, dir.dir)) {
            doMoveVertical(map, start, dir.dir)
            return getNext(start, dir.dir)
        }
    }
    start
}

static def transform(def start, def map, String commands) {
    commands.each { c ->
        def dir = DIR.values().find { it.sign == c }
        start = move(start, dir, map)
    }
}

static long solve(List<String> input) {
    def (start, map, commands) = parse(input)
    transform(start, map, commands)
    def sumOfBoxesGPSCoordinates = 0
    map.size().times { i ->
        map[i].size().times { j ->
            if (map[i][j] == '[') {
                sumOfBoxesGPSCoordinates += i * 100 + j
            }
        }
    }
    sumOfBoxesGPSCoordinates
}

assert 9021L == solve(sampleInput.split('\n') as List)
println solve(new File('input/day15.txt').readLines())