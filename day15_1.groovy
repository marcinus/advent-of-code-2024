import java.lang.StringBuilder

def sampleInputSmall = '''########
#..O.O.#
##@.O..#
#...O..#
#.#.O..#
#...O..#
#......#
########

<^^>>>vv<v>>v<<'''

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

static def parse(List<String> input) {
    int indexOfEmptyLine = input.findIndexOf { it.isEmpty() }
    def map = input.take(indexOfEmptyLine).collect { new StringBuilder(it) }
    int i = (0..<map.size()).find {
        map[it].contains('@')
    }
    def j = map[i].indexOf('@')
    def commands = input.drop(indexOfEmptyLine + 1).join('')
    [[i, j], map, commands]
}

enum DIR {
    UP('^', [-1, 0]), RIGHT('>', [0, 1]), DOWN('v', [1, 0]), LEFT('<', [0, -1])

    def sign
    def dir

    DIR(sign, dir) {
        this.sign = sign
        this.dir = dir
    }
}

static def getAt(def map, def pos) {
    map[pos[0]][pos[1]]
}

static def next(def pos, def dir) {
    [pos[0] + dir[0], pos[1] + dir[1]]
}

// note: modifies map
static def move(def start, def dir, def map) {
    def end = next(start, dir)
    while (getAt(map, end) == 'O') {
        end = next(end, dir)
    }
    if (getAt(map, end) == '.') {
        def closest = next(start, dir)
        if(getAt(map, closest) == 'O') {
            map[end[0]].replace(end[1], end[1]+1, 'O')
        }
        map[start[0]].replace(start[1], start[1]+1, '.')
        map[closest[0]].replace(closest[1], closest[1]+1, '@')
        return closest
    }
    start
}

static def transform(def start, def map, String commands) {
    commands.each { c ->
        def dir = DIR.values().find { it.sign == c }?.dir
        start = move(start, dir, map)
    }
}

static long solve(List<String> input) {
    def (start, map, commands) = parse(input)
    transform(start, map, commands)
    def sumOfBoxesGPSCoordinates = 0
    map.size().times { i ->
        map[i].size().times { j ->
            if (map[i][j] == 'O') {
                sumOfBoxesGPSCoordinates += i * 100 + j
            }
        }
    }
    sumOfBoxesGPSCoordinates
}

assert 2028L == solve(sampleInputSmall.split('\n') as List)
assert 10092L == solve(sampleInput.split('\n') as List)
println solve(new File('input/day15.txt').readLines())