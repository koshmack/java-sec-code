def calculate_average(numbers):
    if not numbers:
        return 0  # Avoid division by zero
    return sum(numbers) / len(numbers)

# Example usage:
my_list = [10, 20, 30, 40]
average = calculate_average(my_list)
print("The calculated average is:", average)
