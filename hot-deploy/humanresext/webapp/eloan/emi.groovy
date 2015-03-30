p = context.loanAmount;
r = context.interest;
n = context.period;
rr = r/(100*12);
nn = n * 12;
x = 1 + rr;
po = Math.pow(x,nn);
num = p * rr * x;
den = po - 1;

emi = num/den;
context.emi = emi;