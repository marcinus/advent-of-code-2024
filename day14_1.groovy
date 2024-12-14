def sampleInput = '''p=0,4 v=3,-3
p=6,3 v=-1,-3
p=10,3 v=-1,2
p=2,0 v=2,-1
p=0,0 v=1,3
p=3,0 v=-2,-2
p=7,6 v=-1,-3
p=3,0 v=-1,-2
p=9,3 v=2,3
p=7,3 v=-1,2
p=2,4 v=2,-3
p=9,5 v=-3,-3'''

static def parse(List<String> input) {
    def ROBOT_PATTERN = ~/p=(-?\d+),(-?\d+) v=(-?\d+),(-?\d+)/
    input.collect {
        def match = (it =~ ROBOT_PATTERN)[0]
        [
                [match[1] as int, match[2] as int],
                [match[3] as int, match[4] as int]
        ]
    }
}

static def quadrant(def p, def v, int k, int n, int m) {
    def newP = [
            Math.floorMod(p[0] + k * v[0], m),
            Math.floorMod(p[1] + k * v[1], n)
    ]
    if(newP[0] == (m/2 as long) || newP[1] == (n/2 as long)) return null
    [
            newP[0] < (m/2 as long),
            newP[1] < (n/2 as long)
    ]
}

static long solve(List<String> input, int k = 100, int n = 103, int m = 101) {
    def data = parse(input)

    data.groupBy {
        quadrant(it[0], it[1], k, n, m)
    }.findAll { it.key != null }
    .collect { it.value.size() }
    .inject(1) { total, el -> el*total } as long
}

assert 12L == solve(sampleInput.split('\n') as List, 100, 7, 11)
println solve(new File('input/day14.txt').readLines())