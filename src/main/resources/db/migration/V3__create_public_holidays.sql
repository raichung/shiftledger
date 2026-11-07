CREATE TABLE public_holidays (
    holiday_date DATE         PRIMARY KEY,
    name         VARCHAR(100) NOT NULL
);

-- South Australian full-day public holidays (source: SafeWork SA).
-- Not included yet: the part-day holidays on Christmas Eve and
-- New Year's Eve (7pm to midnight).
INSERT INTO public_holidays (holiday_date, name) VALUES
    ('2026-01-01', 'New Year''s Day'),
    ('2026-01-26', 'Australia Day'),
    ('2026-03-09', 'Adelaide Cup Day'),
    ('2026-04-03', 'Good Friday'),
    ('2026-04-04', 'Easter Saturday'),
    ('2026-04-05', 'Easter Sunday'),
    ('2026-04-06', 'Easter Monday'),
    ('2026-04-25', 'Anzac Day'),
    ('2026-06-08', 'King''s Birthday'),
    ('2026-10-05', 'Labour Day'),
    ('2026-12-25', 'Christmas Day'),
    ('2026-12-26', 'Proclamation Day'),
    ('2026-12-28', 'Proclamation Day (additional day)'),
    ('2027-01-01', 'New Year''s Day'),
    ('2027-01-26', 'Australia Day'),
    ('2027-03-08', 'Adelaide Cup Day'),
    ('2027-03-26', 'Good Friday'),
    ('2027-03-27', 'Easter Saturday'),
    ('2027-03-28', 'Easter Sunday'),
    ('2027-03-29', 'Easter Monday'),
    ('2027-04-25', 'Anzac Day'),
    ('2027-06-14', 'King''s Birthday'),
    ('2027-10-04', 'Labour Day'),
    ('2027-12-25', 'Christmas Day'),
    ('2027-12-26', 'Proclamation Day'),
    ('2027-12-27', 'Christmas Day (additional day)'),
    ('2027-12-28', 'Proclamation Day (additional day)');