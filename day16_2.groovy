def sampleInput = '''###############
#.......#....E#
#.#.###.#.###.#
#.....#.#...#.#
#.###.#####.#.#
#.#.#.......#.#
#.#.#####.###.#
#...........#.#
###.#.#####.#.#
#...#.....#.#.#
#.#.#.###.#.#.#
#.....#...#.#.#
#.###.#.#.#.#.#
#S..#.....#...#
###############'''

def sampleInput2 = '''#################
#...#...#...#..E#
#.#.#.#.#.#.#.#.#
#.#.#.#...#...#.#
#.#.#.#.###.#.#.#
#...#.#.#.....#.#
#.#.#.#.#.#####.#
#.#...#.#.#.....#
#.#.#####.#.###.#
#.#.#.......#...#
#.#.###.#####.###
#.#.#...#.....#.#
#.#.#.#####.###.#
#.#.#.........#.#
#.#.#.#########.#
#S#.............#
#################'''

enum DIR {
    UP([-1, 0]), RIGHT([0, 1]), DOWN([1, 0]), LEFT([0, -1])

    def dir

    DIR(dir) {
        this.dir = dir
    }

    DIR left() {
        values()[(ordinal() - 1) % (values().length)]
    }

    DIR right() {
        values()[(ordinal() + 1) % (values().length)]
    }
}

static def getNext(def pos, def dir) {
    [pos[0] + dir[0], pos[1] + dir[1]]
}

static def getAt(def map, def pos) {
    map[pos[0]][pos[1]]
}

static def neighbours(def node) {
    [
            [[getNext(node[0], node[1].dir), node[1]], 1],
            [[getNext(node[0], node[1].left().dir), node[1].left()], 1001],
            [[getNext(node[0], node[1].right().dir), node[1].right()], 1001]
    ]
}

static boolean inMap(List<String> map, def pos) {
    pos[0] >= 0 && pos[0] < map.size() &&
    pos[1] >= 0 && pos[1] < map[0].size() &&
    getAt(map, pos) != '#'
}

static long countSeats(List<String> input, def start, def end) {
    def score = [:].withDefault { Long.MAX_VALUE }
    def prev = [:].withDefault { [] as Set }
    def queue = new PriorityQueue({ a, b -> (score[a] - score[b]) as int })
    queue.add([start, DIR.RIGHT])
    score[[start, DIR.RIGHT]] = 0L
    while (!queue.isEmpty()) {
        def u = queue.poll()
        neighbours(u).each { v ->
            if(!inMap(input, v[0][0])) return
            def alt = score[u] + v[1]
            if (alt == score[v[0]]) {
                prev[v[0]] << u
            }
            if (alt < score[v[0]]) {
                prev[v[0]] = [u] as Set
                score[v[0]] = alt
                queue.remove(v[0])
                queue.add(v[0])
            }
        }
    }
    def seats = [] as Set
    def layer = DIR.values().collect { [end, it] } as Queue
    def minDist = layer.collect { score[it]}.min()
    layer = layer.findAll { score[it] == minDist } as Queue
    while(!layer.isEmpty()) {
        def el = layer.poll()
        if(!seats.contains(el)) {
            seats.add(el)
            layer.addAll(prev[el])
        }
    }
    seats.collect { it[0] }.unique().size()
}

static def findStartEnd(List<String> input) {
    def start, end
    input.eachWithIndex {line, i ->
        line.eachWithIndex { x, j ->
            if (x == 'S') start = [i, j]
            else if (x == 'E') end = [i, j]
        }
    }
    [start, end]
}

static long solve(List<String> input) {
    def (start, end) = findStartEnd(input)
    countSeats(input, start, end)
}

assert 45L == solve(sampleInput.split('\n') as List)
assert 64L == solve(sampleInput2.split('\n') as List)
println solve(new File('input/day16.txt').readLines())