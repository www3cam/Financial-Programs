Quandl.auth('EgkfAsfbxwaWyyKZaBCP');

[~,tickers,~] = xlsread('Quandl.csv');%added a two
iterations = length(tickers);
disp(iterations);
ansstruct(iterations).array = [1,0;0,1];

for i = 1:iterations
    try
        matrix = Quandl.get(tickers{i},'type','data');
    catch
        break
    end
    disp(tickers{i})
    ansstruct(i).name = tickers{i};
    ansstruct(i).array = matrix;
end

save('Quandl-dl',ansstruct);
    