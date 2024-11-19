CREATE TABLE vendors (
    vendor_id INT PRIMARY KEY,
    vendor_name VARCHAR(255) NOT NULL
);
CREATE TABLE vendor_details (
    vendor_detail_id INT PRIMARY KEY ,
    vendor_id INT,
    detail_key VARCHAR(255) NOT NULL,
    detail_value VARCHAR(255),
    FOREIGN KEY (vendor_id) REFERENCES vendors(vendor_id)
);
CREATE TABLE vendor_resources_details (
    vendor_resource_id INT PRIMARY KEY ,
    vendor_id INT,
    s3_bucket VARCHAR(255) NOT NULL,
    FOREIGN KEY (vendor_id) REFERENCES vendors(vendor_id)
);
