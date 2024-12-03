def sampleInput = '''xmul(2,4)%&mul[3,7]!@^do_not_mul(5,5)+mul(32,64]then(mul(11,8)mul(8,5))'''

static long solve(String input) {
    def regex = ~/mul\((\d{1,3}),(\d{1,3})\)/
    (input =~ regex).collect {
        (it[1] as long) * (it[2] as long)
    }.sum()
}

assert 161L == solve(sampleInput)
println solve(new File('input/day3.txt').text)