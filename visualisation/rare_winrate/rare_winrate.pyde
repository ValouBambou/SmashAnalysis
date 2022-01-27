def setup():
    background(255)
    size(900, 900)
    characters = []
    n_totals = []
    win_rates = []
    max_total = 0
    min_total = 10000000
    max_win_rate = 0.0
    min_win_rate = 1.0
    # pull data
    with open("../../resources/visualisation_data.csv", "r") as f:
        lines = f.readlines()
        for l in lines:
            values = l.split(",")
            total = int(values[2])
            characters.append(values[0])
            n_totals.append(total)
            win_rate = float(values[3])
            win_rates.append(win_rate)
            if total > max_total:
                max_total = total
            if total < min_total:
                min_total = total
            if win_rate > max_win_rate:
                max_win_rate = win_rate
            if win_rate < min_win_rate:
                min_win_rate = win_rate
    
    print(characters)
    print("min total {}, max total {}".format(min_total, max_total))
    print(n_totals)
    print("min wr {}, max wr {}".format(min_win_rate, max_win_rate))
    print(win_rates)
    avg = 850.0 - (0.5 - min_win_rate) / (max_win_rate - min_win_rate) * 800
    line(450, 50, 450, 850)
    line(50, avg, 850, avg)
    f = createFont("Arial", 16, True)
    textFont(f,16)
    fill(0)
    text("{}".format(min_total), 50, avg)
    text("{}".format(max_total), 800, avg + 20)
    text("{}".format(round(max_win_rate * 100, 2)), 450, 50)
    text("{}".format(round(min_win_rate * 100, 2)), 450, 850)
    text("{}".format(round(0.5 * 100, 2)), 450, avg)
    
    for i in range(len(characters)):
        x = 50 + float(n_totals[i] - min_total) / float(max_total - min_total) * 800
        y = 850.0 - (win_rates[i] - min_win_rate) / (max_win_rate - min_win_rate) * 800
        print(characters[i])
        img = loadImage("../icons/{}.png".format(characters[i]))
        image(img, x, y, 30, 30)
                
    
