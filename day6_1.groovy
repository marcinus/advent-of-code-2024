def sampleInput = '''....#.....
.........#
..........
..#.......
.......#..
..........
.#..^.....
........#.
#.........
......#...'''

enum DIR {
    UP('^', [-1, 0]), RIGHT('>', [0, 1]), DOWN('v', [1, 0]), LEFT('<', [0, -1]), ;

    def sign
    def delta

    DIR(sign, delta) {
        this.sign = sign
        this.delta = delta
    }
}



static def next(List<String> input, def position) {
    def dir = position[2]
    def nextPos = [position[0]+dir.delta[0], position[1]+dir.delta[1], dir]
    if(inBoard(input, nextPos) && input[nextPos[0]][nextPos[1]] == '#') {
        nextPos = [position[0], position[1], DIR.values()[(dir.ordinal()+1)%4]]
    }
    nextPos
}

static def findStart(List<String> input) {
    int n = input.size()
    for (int i = 0; i < n; i++) {
        def dirs = DIR.values();
        for (int k = 0; k < dirs.length; k++) {
            def j = input[i].indexOf(dirs[k].sign)
            if (j >= 0) {
                return [i, j, dirs[k]]
            }
        }
    }
}

static boolean inBoard(List<String> board, def pos) {
    pos[0] >= 0 && pos[0] < board.size() &&
    pos[1] >= 0 && pos[1] < board[0].size()
}

static long solve(List<String> input) {
    Set<List<Integer>> visited = new HashSet<>()
    def pos = findStart(input)
    while (inBoard(input, pos)) {
        visited.add([pos[0], pos[1]])
        pos = next(input, pos)
    }
    visited.size()
}

assert 41L == solve(sampleInput.split('\n') as List)
println solve(new File('input/day6.txt').readLines())