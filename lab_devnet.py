def unique(list1):
    list_set = set(list1)
    unique_list = (list(list_set))
    for x in unique_list:
        print(x)


list1 = ['one', 'two', 'one', '1', 1, '5', 1, '1', 123, 321, 123, 321 , 90, 'Python', 'python', 'Python', 'lecture 4']
print("the unique values from list is")
unique(list1)

