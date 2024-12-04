def sampleInput = '''xmul(2,4)&mul[3,7]!^don't()_mul(5,5)+mul(32,64](mul(11,8)undo()?mul(8,5))'''

static long solve(String input) {
    def regex = ~/(?:mul\((\d{1,3}),(\d{1,3})\))|(do\(\))|(don't\(\))/
    def enable = true
    (input =~ regex).collect {
        if(it[0] == 'do()') {
            enable = true
            return 0L
        }
        if(it[0] == 'don\'t()') {
            enable = false
            return 0L
        }
        if(enable) {
            return (it[1] as long) * (it[2] as long)
        }
        0L
    }.sum()
}

assert 48L == solve(sampleInput)
println solve(new File('input/day3.txt').text)