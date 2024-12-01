def sampleInput = '''3   4
4   3
2   5
1   3
3   9
3   3
'''

static long solves(List<String> lines) {
    def data = lines.collect { line ->
        line.split(' ', 2)
    }
    def firstList = data.collect { it[0].trim() as long }.sort()
    def secondList = data.collect { it[1].trim() as long }.sort()
    firstList.withIndex().collect { long item, int index ->
        Math.abs(item - secondList[index])
    }.sum() as long
}

assert 11L == solves(sampleInput.split('\n') as List)
println solves(new File('input/day1.txt').readLines())