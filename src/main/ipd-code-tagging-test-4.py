# Population numbers (approximate, as of 2025)
china_population = 1411750000
japan_population = 124300000
south_korea_population = 51780000
thailand_population = 71700000
indonesia_population = 279100000
nepal_population = 30700000
india_population = 1426250000

populations = [
    china_population,
    japan_population,
    south_korea_population,
    thailand_population,
    indonesia_population,
    nepal_population,
    india_population
]

total_population = sum(populations)
average_population = total_population / len(populations)

print("Total population:", total_population)
print("Average population:", average_population)